package simu.model;

import eduni.distributions.ContinuousGenerator;
import simu.framework.*;

import javax.print.attribute.standard.QueuedJobCount;
import java.util.LinkedList;

/**
 * Service Point implements the functionalities, calculations and reporting.
 *
 * TODO: This must be modified to actual implementation. Things to be added:
 *     - functionalities of the service point
 *     - measurement variables added
 *     - getters to obtain measurement values
 *
 * Service point has a queue where customers are waiting to be serviced.
 * Service point simulated the servicing time using the given random number generator which
 * generated the given event (customer serviced) for that time.
 *
 * Service point collects measurement parameters.
 */
public class ServicePoint {
	private static class ServiceRecord { //Store a customer currently being serviced and their service completion time
		//Customer currently receiving service
		private Customer customer;
		//Simulation time when customers service will finish
		private double completionTime;

		//Create service record for customer
		public ServiceRecord(Customer customer, double completionTime) {
			this.customer = customer;
			this.completionTime = completionTime;
		}
	}

	//Create Queue record for customer
	private static class QueueRecord {
		private Customer customer;
		private double queueEntryTime;

		public QueueRecord(Customer customer, double queueEntryTime) {
			this.customer = customer;
			this.queueEntryTime = queueEntryTime;
		}
	}

	//Customer waiting for service
	private LinkedList<QueueRecord> queue = new LinkedList<>(); //Waiting queue for customers

	//customersInServiceList = Customers currently being serviced
	//This will prevent 2 doctors from servicing the same customer at the same time
	private LinkedList<ServiceRecord> customersInServiceList = new LinkedList<>();

	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType eventTypeScheduled;
	//Queuestrategy strategy; // option: ordering of the customer

	//Hospital service point information
	private String name;
	private int capacity;
	private int customersInService = 0;
	//Service point statistics
	private double totalWaitingTime = 0;
	private int customersStartedService = 0;
	private int maximumQueueLength = 0;
	private double totalServiceTime = 0;


	/**
	 * Create the service point with a waiting queue.
	 *
	 * @param generator Random number generator for service time simulation
	 * @param eventList Simulator event list, needed for the insertion of service ready event
	 * @param type Event type for the service end event
	 */
	//Constructor for the service point
	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type, String name, int capacity){
		this.eventList = eventList;
		this.generator = generator;
		this.eventTypeScheduled = type;
		this.name = name;
		this.capacity = capacity;
	}

	/**
	 * Add a customer to the service point queue.
	 *
	 * @param a Customer to be queued
	 */
	public void addQueue(Customer a) {
		//Store customer together with the time they entered this queue
		queue.add(new QueueRecord(a, Clock.getInstance().getClock()));

		//Update maximum queue length
		if (queue.size() > maximumQueueLength) {
			maximumQueueLength = queue.size();
		}
	}

	/**
	 * Remove customer from the service point after service completion.
	 * Here we calculate also the appropriate measurement values.
	 *
	 * @return Customer whose service has been completed
	 */
	public Customer removeQueue() {

		//Current simulation time = completion time of this event
		double currentTime = Clock.getInstance().getClock();

		//Find customer that has completed service
		for (int i = 0; i < customersInServiceList.size(); i++) {
			ServiceRecord record = customersInServiceList.get(i);

			if (record.completionTime == currentTime) {
				customersInServiceList.remove(i);
				customersInService--;

				return record.customer;
			}
		}
		return null;
	}

	/**
	 * Begins a new service, by moving a customer from the waiting queue to the in-service list.
	 *
	 * Inserts a new event to the event list when the service should be ready.
	 */
	public void beginService() {
		//Take the first waiting customer from queue
		QueueRecord queueRecord = queue.poll();
		Customer customer = queueRecord.customer;

		//calculate how long the customer waited before service started
		double waitingTime = Clock.getInstance().getClock() - queueRecord.queueEntryTime;

		//Update waiting time statistics
		totalWaitingTime += waitingTime;
		customersStartedService++;

		//Generate service time
		double serviceTime = generator.sample();
		//Update total service time
		totalServiceTime += serviceTime;

		//Calculate when this customer's service will finish
		double completionTime = Clock.getInstance().getClock() + serviceTime;

		//Store customer together with the completion time
		customersInServiceList.add(new ServiceRecord(customer, completionTime));

		Trace.out(Trace.Level.INFO, String.format("Starting %s service for customer #%d at %.2f minutes", name, customer.getId(), Clock.getInstance().getClock()));
		
		customersInService++;

		//schedule the service completion event
		eventList.add(new Event(eventTypeScheduled, completionTime));
	}

	/**
	 * Check whether the service point is busy
	 *
	 * @return logical value indicating service state
	 */
	public boolean isReserved(){
		return customersInService >= capacity;
	}

	/**
	 * Check whether there is customers on the waiting queue
	 *
	 * @return logical value indicating queue status
	 */
	public boolean isOnQueue(){
		return queue.size() != 0;
	}

	//Check if the service point has available capacity
	public boolean hasAvailableCapacity() {
		return customersInService < capacity;
	}

	//Check if there are customers on the waiting queue
	public boolean hasWaitingCustomers() {
		return !queue.isEmpty();
	}

	//Return the name of the service point
	public String getName() {
		return name;
	}

	//Return maximum service capacity of the service point
	public int getCapacity() {
		return capacity;
	}

	//Return the number of customers currently being serviced
	public int getCustomersInService() {
		return customersInService;
	}

	//Return the number of customers currently waiting in the queue
	public int getQueueLength() {
		return queue.size();
	}

	//Return average waiting time for this service point
	public double getAverageWaitingTime() {
		if (customersStartedService == 0) {
			return 0;
		}

		return totalWaitingTime / customersStartedService;
	}

	//Return maximum queue length reached during the simulation
	public int getMaximumQueueLength() {
		return maximumQueueLength;
	}

	//Return  total service time used at this service point
	public double getTotalServiceTime() {
		return totalServiceTime;
	}
	//Return number of customers who started service at this service point
	public int getCustomersStartedService() {
		return customersStartedService;
	}
}
