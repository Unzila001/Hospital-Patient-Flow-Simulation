package simu.model;

import eduni.distributions.ContinuousGenerator;
import simu.framework.*;
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
	private static class ServiceRecord {
		private Customer customer;
		private double completionTime;

		public ServiceRecord(Customer customer, double completionTime) {
			this.customer = customer;
			this.completionTime = completionTime;
		}
	}
	//Customer waiting for service
	private LinkedList<Customer> queue = new LinkedList<>();

	//Customers currently being serviced
	//This will prevent 2 doctors from servicing the same customer at the same time
	private LinkedList<Customer> customersInServiceList = new LinkedList<>();

	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType eventTypeScheduled;
	//Queuestrategy strategy; // option: ordering of the customer

	//Hospital service point information
	private String name;
	private int capacity;
	private int customersInService = 0;

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
	public void addQueue(Customer a) {	// The first customer of the queue is always in service
		queue.add(a);
	}

	/**
	 * Remove customer from the waiting queue.
	 * Here we calculate also the appropriate measurement values.
	 *
	 * @return Customer retrieved from the waiting queue
	 */
	public Customer removeQueue() {		// Remove serviced customer
		customersInService--;
		return customersInServiceList.poll();
	}

	/**
	 * Begins a new service, customer is on the queue during the service
	 *
	 * Inserts a new event to the event list when the service should be ready.
	 */
	public void beginService() {
		//Take the first waiting customer from queue
		Customer customer = queue.poll();

		//Move the customer to the in-service list
		customersInServiceList.add(customer);

		Trace.out(Trace.Level.INFO, "Starting " + name + " service for customer #" + customer.getId());
		
		customersInService++;
		double serviceTime = generator.sample();
		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getClock() + serviceTime));
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
	 * @return logival value indicating queue status
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
}
