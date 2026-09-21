package simu.model;

import eduni.distributions.ContinuousGenerator;
import eduni.distributions.Normal;
import eduni.distributions.Uniform;
import simu.framework.*;
import eduni.distributions.Negexp;

import java.util.Random;

/**
 * Main simulator engine.
 *
 * TODO: This is the place where you implement your own simulator
 *
 * Demo simulation case:
 * Simulate three service points, customer goes through all three service points to get serviced
 * 		--> SP1 --> SP2 --> SP3 -->
 */
public class MyEngine extends Engine {
	private ArrivalProcess arrivalProcess;
	private ServicePoint[] servicePoints;
	public static final boolean TEXTDEMO = true;
	public static final boolean FIXEDARRIVALTIMES = false;
	public static final boolean FXIEDSERVICETIMES = false;

	// ---- statistics collected during the run ----
	private int servedPatients = 0;
	private double totalTimeInHospital = 0;
	private int emergencyPatients = 0;
	private int normalPatients = 0;


	/**
	 * Service Points and random number generator with different distributions are created here.
	 * We use exponent distribution for customer arrival times and normal distribution for the
	 * service times.
	 */
	public MyEngine() {
		//6 hospital service points.
		servicePoints = new ServicePoint[6];

		//Capacity of each hospital service point
		int registrationCapacity = 1;
		int triageCapacity = 1;
		int doctorCapacity = 2;
		int laboratoryCapacity = 1;
		int xrayCapacity = 1;
		int treatmentCapacity = 1;

		if (TEXTDEMO) {
			/* special setup for the example in text
			 * https://github.com/jacquesbergelius/PP-CourseMaterial/blob/master/1.1_Introduction_to_Simulation.md
			 */
			Random r = new Random();

			ContinuousGenerator arrivalTime = null;
			if (FIXEDARRIVALTIMES) {
				/* version where the arrival times are constant (and greater than service times) */

				// make a special "random number distribution" which produces constant value for the customer arrival times
				arrivalTime = new ContinuousGenerator() {
					@Override
					public double sample() {
						return 10;
					}

					@Override
					public void setSeed(long seed) {
					}

					@Override
					public long getSeed() {
						return 0;
					}

					@Override
					public void reseed() {
					}
				};
			} else
				// exponential distribution is used to model customer arrivals times, to get variability between programs runs, give a variable seed
				arrivalTime = new Negexp(10, Integer.toUnsignedLong(r.nextInt()));

			ContinuousGenerator serviceTime = null;
			if (FXIEDSERVICETIMES) {
				// make a special "random number distribution" which produces constant value for the service time in service points
				serviceTime = new ContinuousGenerator() {
					@Override
					public double sample() {
						return 9;
					}

					@Override
					public void setSeed(long seed) {
					}

					@Override
					public long getSeed() {
						return 0;
					}

					@Override
					public void reseed() {
					}
				};
			} else
				// normal distribution used to model service times
				serviceTime = new Normal(10, 6, Integer.toUnsignedLong(r.nextInt()));

			servicePoints[0] = new ServicePoint(serviceTime, eventList, EventType.REGISTRATION_COMPLETE, "Registration", registrationCapacity);
			servicePoints[1] = new ServicePoint(serviceTime, eventList, EventType.TRIAGE_COMPLETE, "Triage", triageCapacity);
			servicePoints[2] = new ServicePoint(serviceTime, eventList, EventType.DOCTOR_COMPLETE, "Doctor", doctorCapacity);
			servicePoints[3] = new ServicePoint(serviceTime, eventList, EventType.XRAY_COMPLETE, "X-ray", xrayCapacity);
			servicePoints[4] = new ServicePoint(serviceTime, eventList, EventType.LABORATORY_COMPLETE, "Laboratory", laboratoryCapacity);
			servicePoints[5] = new ServicePoint(serviceTime, eventList, EventType.TREATMENT_COMPLETE, "Treatment", treatmentCapacity);

			arrivalProcess = new ArrivalProcess(arrivalTime, eventList, EventType.ARRIVAL);
		} else {
			/* more realistic simulation case with variable customer arrival times and service times */
			servicePoints[0] = new ServicePoint(new Normal(10, 6), eventList, EventType.REGISTRATION_COMPLETE, "Registration", registrationCapacity);
			servicePoints[1] = new ServicePoint(new Normal(10, 10), eventList, EventType.TRIAGE_COMPLETE, "Triage", triageCapacity);
			servicePoints[2] = new ServicePoint(new Normal(5, 3), eventList, EventType.DOCTOR_COMPLETE, "Doctor", doctorCapacity);
			servicePoints[3] = new ServicePoint(new Normal(6, 8), eventList, EventType.XRAY_COMPLETE, "X-ray", xrayCapacity);
			servicePoints[4] = new ServicePoint(new Normal(6, 8), eventList, EventType.LABORATORY_COMPLETE, "Laboratory", laboratoryCapacity);
			servicePoints[5] = new ServicePoint(new Normal(3, 10), eventList, EventType.TREATMENT_COMPLETE, "Treatment", treatmentCapacity);


			arrivalProcess = new ArrivalProcess(new Negexp(15, 5), eventList, EventType.ARRIVAL);
		}
	}

	@Override
	protected void initialize() {	// First arrival in the system
		arrivalProcess.generateNextEvent();
	}

	@Override
	protected void runEvent(Event t) {  // B phase events
		Customer a;

		switch ((EventType)t.getType()) {
		case ARRIVAL:
			servicePoints[0].addQueue(new Customer());
			arrivalProcess.generateNextEvent();
			break;

		case REGISTRATION_COMPLETE:
			a = servicePoints[0].removeQueue();
			servicePoints[1].addQueue(a);
			break;

		case TRIAGE_COMPLETE:
			a = servicePoints[1].removeQueue();

			//Triage assigns the patient's condition
			int condition = new Random().nextInt(3);

			if (condition == 0) {
				a.setPatientCondition("minor injury");
			} else if (condition == 1) {
				a.setPatientCondition("serious injury");
			} else {
				a.setPatientCondition("illness");
			}

			//Triage assigns the patient's priority
			boolean emergency = new Random().nextDouble() < 0.20;
			a.setPriority(emergency);
			servicePoints[2].addQueue(a);
			break;

		case DOCTOR_COMPLETE:
			// The doctor's appointment has finished.
			a = servicePoints[2].removeQueue();
			// Generate a random number between 0.0 and 1.0.
			double decision = Math.random();
			if (decision < 0.30) {
				// 30%: send the patient to the laboratory.
				System.out.println("Doctor -> Laboratory");
				servicePoints[4].addQueue(a);
			} else if (decision < 0.50) {
				// 20%: send the patient to X-ray.
				System.out.println("Doctor -> X-ray");
				servicePoints[3].addQueue(a);

			} else if (decision < 0.90) {
				// 40%: send the patient to treatment.
				System.out.println("Doctor -> Treatment");
				servicePoints[5].addQueue(a);

			} else {
				// 10%: discharge the patient directly.
				System.out.println("Doctor -> Direct discharge");
				dischargePatient(a);
			}

			break;

		case LABORATORY_COMPLETE:
         // Laboratoriotutkimus on valmis.
         a = servicePoints[4].removeQueue();

         // Potilas siirtyy laboratorion jälkeen hoitoon.
         servicePoints[5].addQueue(a);
         break;

      	case XRAY_COMPLETE:
         // Röntgenkuvaus on valmis.
         a = servicePoints[3].removeQueue();

         // Potilas siirtyy röntgenin jälkeen hoitoon.
         servicePoints[5].addQueue(a);
         break;

      	case TREATMENT_COMPLETE:
         // Hoito on valmis.
         a = servicePoints[5].removeQueue();

         // Potilas poistuu sairaalasta.
         dischargePatient(a);
         break;
	}
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
		//Generic C phase: starts services while capacity is available
		for (ServicePoint p: servicePoints){
			while (p.isOnQueue() && p.hasAvailableCapacity()){
				p.beginService();
			}
		}
	}

	@Override
	protected void results() {
		System.out.println("\n========== SIMULATION RESULTS ==========");
		System.out.printf("Simulation ended at %.2f minutes%n",
				Clock.getInstance().getClock());
		System.out.println("Patients served: " + servedPatients);

		if (servedPatients > 0) {
			System.out.printf("Average time in hospital: %.2f minutes%n",
					totalTimeInHospital / servedPatients);
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