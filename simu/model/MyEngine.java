package simu.model;

import eduni.distributions.ContinuousGenerator;
import eduni.distributions.Normal;
import eduni.distributions.Negexp;
import simu.framework.*;

import java.util.Random;


public class MyEngine extends Engine {

	private ArrivalProcess arrivalProcess;
	private ServicePoint[] servicePoints;
	private final Random random = new Random();

	// index constants for readability
	private static final int REGISTRATION = 0;
	private static final int TRIAGE = 1;
	private static final int DOCTOR = 2;
	private static final int LAB = 3;
	private static final int XRAY = 4;
	private static final int TREATMENT = 5;

	// probability that an arriving patient is an emergency patient
	private static final double EMERGENCY_PROBABILITY = 0.2;

	// doctor's decision probabilities (must sum to 1.0)
	private static final double PROB_LAB = 0.3;
	private static final double PROB_XRAY = 0.3;
	private static final double PROB_TREATMENT_ONLY = 0.3;
	// remaining probability (0.1) -> direct discharge

	// ---- statistics collected during the run ----
	private int servedPatients = 0;
	private double totalTimeInHospital = 0;
	private int emergencyPatients = 0;
	private int normalPatients = 0;

	public MyEngine() {
		servicePoints = new ServicePoint[6];

		ContinuousGenerator arrivalTime = new Negexp(10, Integer.toUnsignedLong(random.nextInt()));

		servicePoints[REGISTRATION] = new ServicePoint(new Normal(3, 1, Integer.toUnsignedLong(random.nextInt())),
				eventList, EventType.DEP_REGISTRATION, "Registration", 1);
		servicePoints[TRIAGE] = new ServicePoint(new Normal(5, 2, Integer.toUnsignedLong(random.nextInt())),
				eventList, EventType.DEP_TRIAGE, "Triage", 1);
		servicePoints[DOCTOR] = new ServicePoint(new Normal(15, 5, Integer.toUnsignedLong(random.nextInt())),
				eventList, EventType.DEP_DOCTOR, "Doctor", 2);
		servicePoints[LAB] = new ServicePoint(new Normal(10, 3, Integer.toUnsignedLong(random.nextInt())),
				eventList, EventType.DEP_LAB, "Laboratory", 1);
		servicePoints[XRAY] = new ServicePoint(new Normal(15, 4, Integer.toUnsignedLong(random.nextInt())),
				eventList, EventType.DEP_XRAY, "X-ray", 1);
		servicePoints[TREATMENT] = new ServicePoint(new Normal(12, 4, Integer.toUnsignedLong(random.nextInt())),
				eventList, EventType.DEP_TREATMENT, "Treatment", 1);

		arrivalProcess = new ArrivalProcess(arrivalTime, eventList, EventType.ARR1);
	}

	@Override
	protected void initialize() {
		arrivalProcess.generateNextEvent();
	}

	@Override
	protected void runEvent(Event t) {
		Customer a;

		switch ((EventType) t.getType()) {

			case ARR1:
				Customer newPatient = new Customer();
				assignRandomCondition(newPatient);
				assignRandomPriority(newPatient);
				servicePoints[REGISTRATION].addQueue(newPatient);
				arrivalProcess.generateNextEvent();
				break;

			case DEP_REGISTRATION:
				a = servicePoints[REGISTRATION].removeQueue();
				servicePoints[TRIAGE].addQueue(a);
				break;

			case DEP_TRIAGE:
				a = servicePoints[TRIAGE].removeQueue();
				servicePoints[DOCTOR].addQueue(a);
				break;

			case DEP_DOCTOR:
				a = servicePoints[DOCTOR].removeQueue();
				routeAfterDoctor(a);
				break;

			case DEP_LAB:
				a = servicePoints[LAB].removeQueue();
				servicePoints[TREATMENT].addQueue(a);
				break;

			case DEP_XRAY:
				a = servicePoints[XRAY].removeQueue();
				servicePoints[TREATMENT].addQueue(a);
				break;

			case DEP_TREATMENT:
				a = servicePoints[TREATMENT].removeQueue();
				dischargePatient(a);
				break;
		}
	}

	/**
	 * TEMPORARY stub for the Doctor's routing decision.
	 * Replace with Alex's real logic (based on patient condition) when ready.
	 */
	private void routeAfterDoctor(Customer patient) {
		double r = random.nextDouble();

		if (r < PROB_LAB) {
			servicePoints[LAB].addQueue(patient);
		} else if (r < PROB_LAB + PROB_XRAY) {
			servicePoints[XRAY].addQueue(patient);
		} else if (r < PROB_LAB + PROB_XRAY + PROB_TREATMENT_ONLY) {
			servicePoints[TREATMENT].addQueue(patient);
		} else {
			dischargePatient(patient); // direct discharge, no treatment needed
		}
	}

	private void assignRandomCondition(Customer patient) {
		double r = random.nextDouble();
		if (r < 0.5) {
			patient.setPatientCondition("minor injury");
		} else if (r < 0.8) {
			patient.setPatientCondition("illness");
		} else {
			patient.setPatientCondition("serious injury");
		}
	}

	private void assignRandomPriority(Customer patient) {
		boolean isEmergency = random.nextDouble() < EMERGENCY_PROBABILITY;
		patient.setPriority(isEmergency);
	}

	private void dischargePatient(Customer patient) {
		patient.setRemovalTime(Clock.getInstance().getClock());
		patient.setTotalHospitalTime();
		patient.reportResults();

		servedPatients++;
		totalTimeInHospital += patient.getTotalHospitalTime();

		if (patient.hasPriority()) {
			emergencyPatients++;
		} else {
			normalPatients++;
		}
	}

	@Override
	protected void tryCEvents() {
		// Generic C-phase: works for any number of service points.
		for (ServicePoint p : servicePoints) {
			while (p.isOnQueue() && p.hasAvailableCapacity()) {
				p.beginService();
			}
		}
	}

	@Override
	protected void results() {
		System.out.println("\n========== SIMULATION RESULTS ==========");
		System.out.println("Simulation ended at " + Clock.getInstance().getClock());
		System.out.println("Patients served: " + servedPatients);

		if (servedPatients > 0) {
			System.out.println("Average time in hospital: "
					+ (totalTimeInHospital / servedPatients));
		} else {
			System.out.println("Average time in hospital: N/A (no patients served)");
		}

		System.out.println("Emergency patients served: " + emergencyPatients);
		System.out.println("Normal patients served: " + normalPatients);

		System.out.println("\n---- Service point snapshot (at end of run) ----");
		for (ServicePoint p : servicePoints) {
			System.out.println(p.getName()
					+ " | capacity: " + p.getCapacity()
					+ " | in service: " + p.getCustomersInService()
					+ " | still waiting in queue: " + p.getQueueLength());
		}
		System.out.println("=========================================\n");
	}
}
