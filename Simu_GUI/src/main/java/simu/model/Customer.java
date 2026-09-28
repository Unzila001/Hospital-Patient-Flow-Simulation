package simu.model;

import simu.framework.*;

/**
 * Customer in a simulator
 *
 * TODO: This is to be implemented according to the requirements of the simulation model (data!)
 */

public class Customer {
	private double arrivalTime;
	private double removalTime;
	private double totalHospitalTime;
	private String patientCondition = "";
	private boolean priority;
	private int id;
	private static int i = 1;
	private static double sum = 0;
	private static long minorInjury;
	private static long seriousInjury;
	private static long illness;
	private static int completedPatients;

	/**
	 * Create a unique customer
	 */
	public Customer() {
		id = i++;

		arrivalTime = Clock.getInstance().getTime();
		Trace.out(Trace.Level.INFO, "New customer #" + id + " arrived at  " + arrivalTime);
	}

	/**
	 * Give the time when customer has been removed (from the system to be simulated)
	 * @return Customer removal time
	 */
	public double getRemovalTime() {
		return removalTime;
	}

	/**
	 * Mark the time when the customer has been removed (from the system to be simulated)
	 * @param removalTime Customer removal time
	 */
	public void setRemovalTime(double removalTime) {
		this.removalTime = removalTime;
	}

	/**
	 * Give the time when the customer arrived to the system to be simulated
	 * @return Customer arrival time
	 */
	public double getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * Mark the time when the customer arrived to the system to be simulated
	 * @param arrivalTime Customer arrival time
	 */
	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * Calculate the total time the customer was in the hospital
	 */
	public void setTotalHospitalTime() { totalHospitalTime = removalTime - arrivalTime; }


	/**
	 * Give the total time the customer was in the hospital
	 * @return Customer total hospital time
	 */
	public double getTotalHospitalTime() { return totalHospitalTime; }

	/**
	 * Sets the patients medical condition
	 * @param patientCondition Customer medical condition (serious injury, minor injury, illness)
	 */
	public void setPatientCondition (String patientCondition) {
		if(patientCondition != null && (patientCondition.equals("minor injury") || patientCondition.equals("serious injury") || patientCondition.equals("illness"))){
			this.patientCondition = patientCondition;
		} else {
			System.out.println("Invalid patient condition!");
		}
	}

	/**
	 * Give the patients condition
	 * @return Customers condition
	 */
	public String getPatientCondition() { return patientCondition; }

	/**
	 * Set the patients priority
	 */
	public void setPriority(boolean priority) {
		this.priority = priority;
	}

	/**
	 * Checks whether the patient has priority
	 */
	public boolean hasPriority() { return priority; }

	/**
	 * Get the (unique) customer id
	 * @return Customer id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Report the measured variables of the customer. In this case to the diagnostic output.
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