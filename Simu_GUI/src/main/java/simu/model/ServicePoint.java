package simu.model;

import controller.Controller;
import dao.ServicePointDao;
import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

import java.util.PriorityQueue;
import java.util.LinkedList;

/**
 * Represents a service point in the hospital patient flow simulation.
 *
 * Manages patients waiting in a queue, handles patient priorities,
 * controls service capacity, and schedules service completion events.
 *
 * Also collects statistics such as waiting times, queue lengths,
 * and service utilization.
 */
public class ServicePoint {

	/**
	 * Waiting queue for patients at this service point.
	 * Patients are ordered according to priority and arrival order.
	 */
	private LinkedList<Customer> jono = new LinkedList<Customer>(); // Data Structure used

	/**
	 * Generates random service durations for patients
	 * at this service point.
	 */
	private ContinuousGenerator generator;

	/**
	 * Stores scheduled simulation events, including
	 * service completion events for this service point.
	 */
	private EventList eventList;

	/**
	 * Defines the event type generated when a patient finishes service.
	 */
	private EventType eventTypeScheduled;

	/**
	 * Controller responsible for updating patient waiting status.
	 */
	private Controller controller;

	/**
	 * Name of the service point, such as Registration, Triage, or Doctor.
	 */
	private String name;

	/**
	 * Maximum number of patients who can receive service simultaneously.
	 */
	private int capacity;

	/**
	 * Number of patients currently receiving service.
	 */
	private int customersInService = 0;

	/**
	 * Total waiting time accumulated by patients before starting service.
	 */
	private double totalWaitingTime = 0;

	/**
	 * Highest number of patients recorded in the waiting queue.
	 */
	private int maximumQueueLength = 0;

	/**
	 * Total number of patients who have started service.
	 */
    private int customersStartedService = 0;

	/**
	 * Total generated service duration accumulated for patients.
	 */
	private double totalServiceTime = 0;

	/**
	 * Records the time each patient entered the waiting queue.
	 * Used to calculate patient waiting times.
	 */
	private final java.util.Map<Customer, Double> queueEntryTimes = new java.util.IdentityHashMap<>();

	/**
	 * Represents a patient currently receiving service
	 * and the scheduled time when their service will finish.
	 */
	private static class CustomerInService {

		/**
		 * Patient currently receiving service.
		 */
		private Customer customer;

		/**
		 * Simulation time when the patient's service will finish.
		 */
		private double completionTime;

		/**
		 * Creates a record for a patient receiving service.
		 *
		 * @param customer patient receiving service
		 * @param completionTime scheduled service completion time
		 */
		public CustomerInService(Customer customer, double completionTime) {
			this.customer = customer;
			this.completionTime = completionTime;
		}
	}

	/**
	 * Stores patients currently receiving service, ordered
	 * by their scheduled completion times.
	 */
	private PriorityQueue<CustomerInService> customersBeingServed = new PriorityQueue<>(
			(a, b) -> Double.compare(a.completionTime, b.completionTime));

	/**
	 * Creates a service point for the hospital simulation.
	 *
	 * @param generator generates the service time for each patient
	 * @param tapahtumalista event list used to schedule service completion
	 * @param tyyppi type of event scheduled when service is completed
	 * @param name name of the service point
	 * @param capacity maximum number of patients served simultaneously
	 * @param controller controller used to update patient waiting status
	 */

	public ServicePoint(ContinuousGenerator generator, EventList tapahtumalista, EventType tyyppi, String name, int capacity, Controller controller){
		this.eventList = tapahtumalista;
		this.generator = generator;
		this.eventTypeScheduled = tyyppi;
		this.name = name;
		this.capacity = Math.max(1, capacity);
		this.controller = controller;
	}

	/**
	 * Adds a patient to the waiting queue.
	 *
	 * Priority patients are placed before normal patients.
	 * Patients with the same priority are served in FIFO
	 * (First In, First Out) order.
	 *
	 * Records the patient's queue entry time and updates
	 * the maximum queue length.
	 *
	 * @param customer the patient to add to the waiting queue
	 */

	public void addQueue(Customer customer){
		//Patient is now waiting
		controller.updateWaitingStatus(customer.getId(), true);

        queueEntryTimes.put(customer, Clock.getInstance().getTime());
		if (customer.hasPriority()) {
			int position = 0;

			//Keep the FIFO order between priority customers
			while (position < jono.size()
					&& jono.get(position).hasPriority()) {
				position++;
			}
			jono.add(position, customer);
		} else {
			//Normal customer will join the end of the queue
			jono.addLast(customer);
		}
	    maximumQueueLength = Math.max(maximumQueueLength, jono.size());
	}

	/**
	 * Removes and returns the patient whose service has finished.
	 *
	 * The patient with the earliest scheduled completion time
	 * is removed from the list of patients being served.
	 * One service position becomes available.
	 *
	 * @return the patient whose service has completed,
	 *         or null if no patient is currently being served
	 */

	public Customer removeQueue(){		//removes completed patient from service

		//Get the customer with earliest completion time
		CustomerInService completed = customersBeingServed.poll();

		//Safety check
		if (completed == null) {
			return null;
		}

		//Free one service position
		if (customersInService > 0) {
			customersInService --;
		}

		//return customer whose service finished
		return completed.customer;
	}

	/**
	 * Starts service for waiting patients when capacity is available.
	 *
	 * Removes patients from the waiting queue in priority order
	 * and records their waiting times.
	 *
	 * Generates a service duration for each patient and calculates
	 * the scheduled completion time.
	 *
	 * Updates service statistics and schedules a completion event
	 * in the simulation event list.
	 */

	public void beginService() {		//moves patient from waiting to service

		//Start service only if capacity is available
		//and there is customer waiting
		while (customersInService < capacity && !jono.isEmpty()) {

			//Remove the first waiting customer
			Customer customer = jono.poll();

			// Patient has left the waiting queue and started service
			controller.updateWaitingStatus(customer.getId(), false);

            Double entered = queueEntryTimes.remove(customer);
            if (entered != null) totalWaitingTime += Clock.getInstance().getTime() - entered;
            customersStartedService++;

			//Generate service time
			double serviceTime = generator.sample();
            totalServiceTime += serviceTime;

			//calculate when this customer's service will finish
			double completionTime = Clock.getInstance().getTime() + serviceTime;

			//Store customer as currently being served
			customersBeingServed.add(
					new CustomerInService(customer, completionTime)
			);

			//Increase number of occupied service positions
			customersInService++;

			//Schedule complete event
			eventList.add(new Event(eventTypeScheduled, completionTime));
		}
	}

	/**
	 * Checks whether the service point is operating at full capacity.
	 *
	 * @return true if all service positions are occupied,
	 *         false if at least one position is available
	 */

	public boolean isReserved(){ 	//Check if the service point has reached its full capacity;
		return customersInService >= capacity;
	}

	/**
	 * Checks whether any patients are waiting in the queue.
	 *
	 * @return true if the waiting queue contains patients,
	 *         false if the queue is empty
	 */

	public boolean isOnQueue() {	//Check if there are customers in queue
		return !jono.isEmpty();
	}

	/**
	 * Returns the number of patients currently waiting in the queue.
	 *
	 * @return the current waiting queue size
	 */

	public int getQueueSize(){		//Return the current number of customers in the queue
		return jono.size();
	}

	/**
	 * Returns the number of patients currently receiving service.
	 *
	 * @return the number of patients in service
	 */

	public int getCustomersInService() {	//Return the number of customers currently being served
		return customersInService;
	}

	/**
	 * Returns the name of this service point.
	 *
	 * @return the service point name
	 */

	public String getName() {		//Return the name of service point
		return name;
	}

	/**
	 * Returns the maximum number of patients that can
	 * receive service simultaneously.
	 *
	 * @return the service point capacity
	 */

	public int getCapacity() {		//Return the maximum service capacity
		return capacity;
	}

	/**
	 * Calculates the average waiting time of patients who have started service.
	 *
	 * Divides the total waiting time by the number of patients
	 * who have started service. Returns zero if no patient
	 * has started service.
	 *
	 * @return the average patient waiting time in simulation minutes
	 */

	public double getAverageWaitingTime() { return customersStartedService == 0 ? 0 : totalWaitingTime / customersStartedService; }

	/**
	 * Returns the maximum number of patients that have
	 * been waiting in the queue at the same time.
	 *
	 * @return the maximum recorded queue length
	 */

	public int getMaximumQueueLength() { return maximumQueueLength; }

	/**
	 * Returns the total service time accumulated for patients
	 * who have started service at this service point.
	 *
	 * @return the total generated service time in simulation minutes
	 */
	public double getTotalServiceTime() { return totalServiceTime; }

	/**
	 * Calculates the utilization percentage of this service point
	 * using the current simulation clock time.
	 *
	 * Utilization is calculated by dividing the total generated
	 * service time by the available service capacity over time.
	 *
	 * @return the service point utilization percentage
	 */
	public double getUtilization() { return getTotalServiceTime() / (Clock.getInstance().getTime() * getCapacity()) * 100; }

	/**
	 * Returns the total number of patients who have started
	 * receiving service at this service point.
	 *
	 * @return the number of patients who have started service
	 */
	public int getCustomersStartedService() { return customersStartedService; }

	/**
	 * Returns the current number of patients waiting
	 * in the service point queue.
	 *
	 * @return the current queue length
	 */
	public int getQueueLength() { return jono.size(); }

	/**
	 * Calculates the utilization percentage of this service point
	 * using the specified simulation time.
	 *
	 * Utilization is calculated from the total generated service
	 * time, simulation duration, and service capacity.
	 *
	 * @param simulationTime the simulation duration in minutes
	 * @return the calculated utilization percentage,
	 *         or zero if simulation time is zero or negative
	 */
	public double getUtilization(double simulationTime) {

		if (simulationTime <= 0) {
			return 0;
		}
		return (totalServiceTime / (simulationTime * capacity)) * 100;
	}
}
