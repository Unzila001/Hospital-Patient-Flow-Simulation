package simu.model;

import simu.framework.*;

/**
 * Represents a customer in the simulation.
 *
 * A customer has an arrival time, removal time, a medical condition,
 * and a priority status. The class also tracks statistics about completed
 * patients and their time spent in the hospital.
 */

public class Customer {

    /**
     * The time the customer arrived at the simulation.
     */
	private double arrivalTime;

    /**
     * The time the customer left the simulation.
     */
	private double removalTime;

    /**
     * The total hospital time of the customer.
     */
	private double totalHospitalTime;

    /**
     * Customer's condition.
     */
	private String patientCondition = "";

    /**
     * Customer's priority.
     */
	private boolean priority;

    /**
     * Customer's ID.
     */
	private int id;

    /**
     * The next unique ID to be assigned to a customer.
     */
	private static int i = 1;

    /**
     * The cumulative total of time all customers have spent in the hospital.
	 * Used to calculate the mean hospital time.
     */
	private static double sum = 0;

    /**
     * The amount of customers with minor injury.
     */
	private static long minorInjury;

    /**
     * The amount of customers with serious injury.
     */
	private static long seriousInjury;

    /**
     * The amount of customers with serious illness.
     */
	private static long illness;

    /**
     * The amount of customers who progressed fully through the simulation.
     */
	private static int completedPatients;

	/**
	 * Creates a new customer object with a unique ID.
	 */
	public Customer() {
		id = i++;

		arrivalTime = Clock.getInstance().getTime();
		Trace.out(Trace.Level.INFO, "New customer #" + id + " arrived at  " + arrivalTime);
	}

	/**
	 * Gets the time at which the customer was removed.
	 * @return Customer's removal time
	 */
	public double getRemovalTime() {
		return removalTime;
	}

	/**
	 * Sets the time at which the customer was removed.
	 * @param removalTime Customer's removal time
	 */
	public void setRemovalTime(double removalTime) {
		this.removalTime = removalTime;
	}

	/**
	 * Gets the time when the customer arrived to the system.
	 * @return Customer's arrival time
	 */
	public double getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * Sets the time when the customer arrived to the system.
	 * @param arrivalTime Customer's arrival time
	 */
	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * Calculate the total time the customer was in the hospital.
	 */
	public void setTotalHospitalTime() { totalHospitalTime = removalTime - arrivalTime; }


	/**
	 * Gets the total time the customer was in the hospital.
	 * @return Customer's total hospital time
	 */
	public double getTotalHospitalTime() { return totalHospitalTime; }

	/**
	 * Sets the customer's medical condition.
	 * @param patientCondition Customer's medical condition (serious injury, minor injury, illness)
	 */
	public void setPatientCondition (String patientCondition) {
		if(patientCondition != null && (patientCondition.equals("minor injury") || patientCondition.equals("serious injury") || patientCondition.equals("illness"))){
			this.patientCondition = patientCondition;
		} else {
			System.out.println("Invalid patient condition!");
		}
	}

	/**
	 * Gets the customer's medical condition.
	 * @return Customer's condition
	 */
	public String getPatientCondition() { return patientCondition; }

	/**
	 * Sets the customer's priority.
	 */
	public void setPriority(boolean priority) {
		this.priority = priority;
	}

	/**
	 * Checks whether the customer has priority.
	 */
	public boolean hasPriority() { return priority; }

	/**
	 * Gets the customer's unique ID.
	 * @return Customer's id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Reports the measured variables of the customer. In this case to the diagnostic output.
	 */
	public void reportResults() {
		Trace.out(Trace.Level.INFO, "\nCustomer " + id + " ready! ");
		Trace.out(Trace.Level.INFO, String.format("New customer #%d arrived at %.2f minutes.", id, arrivalTime));
		Trace.out(Trace.Level.INFO, String.format("Customer #%d removed at %.2f minutes.", id, removalTime));
		Trace.out(Trace.Level.INFO, String.format("Customer #%d stayed %.2f minutes.", id, totalHospitalTime));
		Trace.out(Trace.Level.INFO,"Customer "    + id + " had condition: "  + patientCondition);

		if(priority) {
			Trace.out(Trace.Level.INFO,"Customer "    + id + " had priority.");
		} else {
			Trace.out(Trace.Level.INFO,"Customer "    + id + " didn't have priority.");
		}

		completedPatients++;
		sum += totalHospitalTime;
		double mean = sum / completedPatients;
		System.out.printf("Current mean patient time in hospital: %.2f minutes.%n", mean);

		if(patientCondition.equals("minor injury")){
			minorInjury ++;
		} else if(patientCondition.equals("serious injury")){
			seriousInjury ++;
		} else {
			illness ++;
		}

		System.out.println("Current amount of patients with minor injuries: " + minorInjury);
		System.out.println("Current amount of patients with serious injury: " + seriousInjury);
		System.out.println("Current amount of patients with illnesses: " + illness);

	}
}