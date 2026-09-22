package simu.model;

import controller.IControllerMtoV;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.ArrivalProcess;
import simu.framework.Clock;
import simu.framework.Engine;
import simu.framework.Event;


public class MyEngine extends Engine {
	private ArrivalProcess arrivalProcess;

	//Service point capacities
	private final int registrationCapacity = 1;
	private final int triageCapacity = 1;
	private final int doctorCapacity = 2;
	private final int xrayCapacity = 1;
	private final int laboratoryCapacity = 1;
	private final int treatmentCapacity = 1;

	public MyEngine(IControllerMtoV controller){ // NEW
		super(controller); // NEW

		servicePoints = new ServicePoint[6];

		servicePoints[0] = new ServicePoint(new Normal(10,6), eventList, EventType.REGISTRATION_COMPLETE, "Registration", registrationCapacity);
		servicePoints[1] = new ServicePoint(new Normal(10,6), eventList, EventType.TRIAGE_COMPLETE, "Triage", triageCapacity);
		servicePoints[2] = new ServicePoint(new Normal(10,6), eventList, EventType.DOCTOR_COMPLETE, "Doctor", doctorCapacity);
		servicePoints[3] = new ServicePoint(new Normal(10,6), eventList, EventType.XRAY_COMPLETE, "X-ray", xrayCapacity);
		servicePoints[4] = new ServicePoint(new Normal(10,6), eventList, EventType.LABORATORY_COMPLETE, "Laboratory", laboratoryCapacity);
		servicePoints[5] = new ServicePoint(new Normal(10,6), eventList, EventType.TREATMENT_COMPLETE, "Treatment", treatmentCapacity);

		arrivalProcess = new ArrivalProcess(new Negexp(15,5), eventList, EventType.ARRIVAL);
	}

	@Override
	protected void initialization() {
		arrivalProcess.generateNext();	 // First arrival in the system
	}

	@Override
	protected void runEvent(Event t) {  // B phase events
		Customer a;

		switch ((EventType)t.getType()) {
			case ARRIVAL:
				servicePoints[0].addQueue(new Customer());
				arrivalProcess.generateNext();
				controller.visualiseCustomer(); // NEW
				break;

			case REGISTRATION_COMPLETE:
				a = servicePoints[0].removeQueue();
				servicePoints[1].addQueue(a);
				break;
		}
	}

	@Override
	protected void results() {
		// OLD text UI
		//System.out.println("Simulation ended at " + Clock.getInstance().getClock());
		//System.out.println("Results ... are currently missing");

		// NEW GUI
		controller.showEndTime(Clock.getInstance().getTime());
	}
}
