package simu.model;

import controller.Controller;
import controller.IControllerMtoV;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.ArrivalProcess;
import simu.framework.Clock;
import simu.framework.Engine;
import simu.framework.Event;


public class MyEngine extends Engine {
    private ArrivalProcess arrivalProcess;
    private Controller logicController;

    // Simulation results
    private int servedPatients = 0;
    private double totalTimeInHospital = 0;
    private int emergencyPatients = 0;
    private int normalPatients = 0;

    //Service point capacities
    private final int registrationCapacity = 1;
    private final int triageCapacity = 1;
    private final int doctorCapacity = 2;
    private final int xrayCapacity = 1;
    private final int laboratoryCapacity = 1;
    private final int treatmentCapacity = 1;

    public MyEngine(IControllerMtoV controller) { // NEW
        super(controller);// NEW

        this.logicController = (Controller) controller;

        servicePoints = new ServicePoint[6];

        servicePoints[0] = new ServicePoint(new Normal(10, 6), eventList, EventType.REGISTRATION_COMPLETE, "Registration", registrationCapacity, logicController);
        servicePoints[1] = new ServicePoint(new Normal(10, 6), eventList, EventType.TRIAGE_COMPLETE, "Triage", triageCapacity, logicController);
        servicePoints[2] = new ServicePoint(new Normal(10, 6), eventList, EventType.DOCTOR_COMPLETE, "Doctor", doctorCapacity, logicController);
        servicePoints[3] = new ServicePoint(new Normal(10, 6), eventList, EventType.XRAY_COMPLETE, "X-ray", xrayCapacity, logicController);
        servicePoints[4] = new ServicePoint(new Normal(10, 6), eventList, EventType.LABORATORY_COMPLETE, "Laboratory", laboratoryCapacity, logicController);
        servicePoints[5] = new ServicePoint(new Normal(10, 6), eventList, EventType.TREATMENT_COMPLETE, "Treatment", treatmentCapacity, logicController);

        arrivalProcess = new ArrivalProcess(new Negexp(15, 5), eventList, EventType.ARRIVAL);
    }

    @Override
    protected void initialization() {
        arrivalProcess.generateNext();     // First arrival in the system
    }

    @Override
    protected void runEvent(Event t) {  // B phase events
        Customer a;

        switch ((EventType) t.getType()) {
            case ARRIVAL:
                Customer newCustomer = new Customer();

                //Show the new patient arriving
                controller.visualiseCustomer(newCustomer);

                //Patient enters the Registration queue
                servicePoints[0].addQueue(newCustomer);

                //Move patient visually to Registration
                controller.moveCustomer(newCustomer.getId(), "Registration");

                //MUISTA LAITTAA NÄMÄ KOSKA TÄMÄ ON SE JONOTUS LOGIIKKA!
                servicePoints[0].beginService();

                //Generate next arrival
                arrivalProcess.generateNext();
                break;

            case REGISTRATION_COMPLETE:
                a = servicePoints[0].removeQueue();
                servicePoints[1].addQueue(a);

                //Move patient visually to Triage
                controller.moveCustomer(a.getId(), "Triage");

                servicePoints[1].beginService();

                break;

            case TRIAGE_COMPLETE:
                a = servicePoints[1].removeQueue();

                //Assign patient condition during triage
                int condition = (int) (Math.random() * 100);

                if (condition < 50) {
                    //50% illness
                    a.setPatientCondition("illness");
                    a.setPriority(false);

                } else if (condition < 80) {
                    //30% minor injury
                    a.setPatientCondition("minor injury");
                    a.setPriority(false);

                } else {
                    //20% serious injury
                    a.setPatientCondition("serious injury");
                    a.setPriority(true);
                }

                //Update condition and priority in visualization
                controller.updatePatientState(
                        a.getId(),
                        a.getPatientCondition(),
                        a.hasPriority()
                );

                //Move patient from Triage to Doctor
                servicePoints[2].addQueue(a);

                //Update visualization
                controller.moveCustomer(a.getId(), "Doctor");

                servicePoints[2].beginService();

                break;

            case DOCTOR_COMPLETE:
                a = servicePoints[2].removeQueue();

                int destination = (int) (Math.random() * 100);

                if (destination < 30) {
                    // 30 %: laboratorio
                    servicePoints[4].addQueue(a);
                    controller.moveCustomer(a.getId(), "Laboratory");
                    servicePoints[4].beginService();

                } else if (destination < 50) {
                    // 20 %: röntgen
                    servicePoints[3].addQueue(a);
                    controller.moveCustomer(a.getId(), "X-ray");
                    servicePoints[3].beginService();

                } else if (destination < 90) {
                    // 40 %: hoito
                    servicePoints[5].addQueue(a);
                    controller.moveCustomer(a.getId(), "Treatment");
                    servicePoints[5].beginService();

                } else {
                    // 10 %: potilas poistuu sairaalasta
                    a.setRemovalTime(Clock.getInstance().getTime());
                    a.setTotalHospitalTime();
                    a.reportResults();
                    recordCompletedPatient(a);
                    controller.moveCustomer(a.getId(), "Exit");
                }
                break;

            case LABORATORY_COMPLETE:
                a = servicePoints[4].removeQueue();
                servicePoints[5].addQueue(a);
                controller.moveCustomer(a.getId(), "Treatment");
                servicePoints[5].beginService();
                break;

            case XRAY_COMPLETE:
                a = servicePoints[3].removeQueue();
                servicePoints[5].addQueue(a);
                controller.moveCustomer(a.getId(), "Treatment");
                servicePoints[5].beginService();
                break;

            case TREATMENT_COMPLETE:
                a = servicePoints[5].removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                a.setTotalHospitalTime();
                a.reportResults();
                recordCompletedPatient(a);
                controller.moveCustomer(a.getId(), "Exit");
                break;
        }
    }

    private void recordCompletedPatient(Customer patient) {
        servedPatients++;
        totalTimeInHospital += patient.getTotalHospitalTime();

        if (patient.hasPriority()) {
            emergencyPatients++;
        } else {
            normalPatients++;
        }
    }

    @Override
    protected void results() {
        double simulationTime = Clock.getInstance().getTime();

        // Säilytetään yhteys GUI-käyttöliittymään.
        controller.showEndTime(simulationTime);

        System.out.println("\n========== SIMULATION RESULTS ==========");

        // 1. Hoidetut potilaat
        System.out.println("Patients served: " + servedPatients);

        // 2. Keskimääräinen sairaalassaoloaika
        double averageHospitalTime = servedPatients == 0
                ? 0
                : totalTimeInHospital / servedPatients;

        System.out.printf(
                "Average time in hospital: %.2f minutes%n",
                averageHospitalTime
        );

        // 3. Potilaita tunnissa
        double patientsPerHour = simulationTime <= 0
                ? 0
                : servedPatients / (simulationTime / 60.0);

        System.out.printf(
                "Patients per hour: %.2f%n",
                patientsPerHour
        );

        // 4. Lääkärin käyttöaste
        ServicePoint doctor = servicePoints[2];

        double doctorUtilization = simulationTime <= 0
                ? 0
                : doctor.getTotalServiceTime()
                / (simulationTime * doctor.getCapacity()) * 100;

        System.out.printf(
                "Doctor utilization: %.2f%%%n",
                doctorUtilization
        );

        // 5. Jokaisen palvelupisteen tilastot
        System.out.println("\n--- SERVICE POINT STATISTICS ---");

        for (ServicePoint point : servicePoints) {
            System.out.println("\n" + point.getName());

            System.out.printf(
                    "Average waiting time: %.2f minutes%n",
                    point.getAverageWaitingTime()
            );

            System.out.println(
                    "Maximum queue length: " + point.getMaximumQueueLength()
            );
        }

        System.out.println("\n========================================");
    }
}
