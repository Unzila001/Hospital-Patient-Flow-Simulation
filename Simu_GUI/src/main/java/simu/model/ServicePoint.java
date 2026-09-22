package simu.model;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

import java.util.PriorityQueue;
import java.util.LinkedList;

// TODO:
// Service Point functionalities & calculations (+ variables needed) and reporting to be implemented
public class ServicePoint {
	private LinkedList<Customer> jono = new LinkedList<Customer>(); // Data Structure used
	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType eventTypeScheduled;
	//Queuestrategy strategy; // option: ordering of the customer

	//Service point information
	private String name;
	private int capacity;
	private int customersInService = 0;

	//Store customers currently being served and their completion times
	private static class CustomerInService {

		private Customer customer;
		private double completionTime;

		public CustomerInService(Customer customer, double completionTime) {
			this.customer = customer;
			this.completionTime = completionTime;
		}
	}

	//Customers currently being served, ordered by finishing time
	private PriorityQueue<CustomerInService> customersBeingServed = new PriorityQueue<>(
			(a, b) -> Double.compare(a.completionTime, b.completionTime));

	public ServicePoint(ContinuousGenerator generator, EventList tapahtumalista, EventType tyyppi, String name, int capacity){
		this.eventList = tapahtumalista;
		this.generator = generator;
		this.eventTypeScheduled = tyyppi;
		this.name = name;
		this.capacity = Math.max(1, capacity);
	}

	//Add patient to the waiting queue
	//Priority customers are placed before non-priority customers
	public void addQueue(Customer customer){
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
	}

	//Remove and return the customer whose service has been completed
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

	//Start service for waiting customers while capacity is available
	public void beginService() {		//moves patient from waiting to service

		//Start service only if capacity is available
		//and there is customer waiting
		while (customersInService < capacity && !jono.isEmpty()) {

			//Remove the first waiting customer
			Customer customer = jono.poll();

			//Generate service time
			double serviceTime = generator.sample();

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

	public boolean isReserved(){ 	//Check if the service point has reached its full capacity;
		return customersInService >= capacity;
	}

	public boolean isOnQueue() {	//Check if there are customers in queue
		return !jono.isEmpty();
	}

	public int getQueueSize(){		//Return the current number of customers in the queue
		return jono.size();
	}

	public int getCustomersInService() {	//Return the number of customers currently being served
		return customersInService;
	}

	public String getName() {		//Return the name of service point
		return name;
	}

	public int getCapacity() {		//Return the maximum service capacity
		return capacity;
	}
}
