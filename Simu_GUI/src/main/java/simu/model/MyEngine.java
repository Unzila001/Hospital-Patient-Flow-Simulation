package simu.model;

import dao.PatientResultDao;
import controller.Controller;
import controller.IControllerMtoV;
import dao.ServicePointResultDao;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.ArrivalProcess;
import simu.framework.Clock;
import simu.framework.Engine;
import simu.framework.Event;

import java.util.ArrayList;
import java.util.List;

import dao.SimulationRunDao;

import java.sql.SQLException;
import java.util.Random;

/**
 * Implements the hospital patient flow simulation engine.
 * <p>
 * Manages patient arrivals, service point transitions,
 * patient conditions, priorities and simulation results.
 * <p>
 * The engine uses a discrete-event simulation model
 * and stores completed simulation results in a database.
 * <p>
 * Extends the Engine framework class.
 */
public class MyEngine extends Engine {

    /**
     * Stores patients who have completed their hospital visit.
     */
    private final List<Customer> completedCustomers = new ArrayList<>();

    /**
     * Maximum number of patients served simultaneously at Registration.
     */
    private final int registrationCapacity = 1;

    /**
     * Maximum number of patients served simultaneously at Triage.
     */
    private final int triageCapacity = 1;

    /**
     * Maximum number of patients served simultaneously at Doctor.
     */
    private final int doctorCapacity = 2;

    /**
     * Maximum number of patients served simultaneously at X-ray.
     */
    private final int xrayCapacity = 1;

    /**
     * Maximum number of patients served simultaneously at Laboratory.
     */
    private final int laboratoryCapacity = 1;

    /**
     * Maximum number of patients served simultaneously at Treatment.
     */
    private final int treatmentCapacity = 1;

    /**
     * Generates patient arrival events during the simulation.
     */
    private ArrivalProcess arrivalProcess;

    /**
     * Controller used to communicate with the graphical user interface.
     */
    private Controller logicController;

    /**
     * Holds the calculated statistics of a service point.
     */
    private ServicePointStats stats;

    /**
     * Provides database operations for service point results.
     */
    private ServicePointResultDao servicePointResultDao = new ServicePointResultDao();

    /**
     * Provides database operations for completed patient results.
     */
    private PatientResultDao patientResultDao = new PatientResultDao();

    /**
     * Total number of patients who have completed their hospital visit.
     */
    private int servedPatients = 0;

    /**
     * Sum of hospital visit durations for all completed patients.
     */
    private double totalTimeInHospital = 0;

    /**
     * Number of completed patients classified as emergencies.
     */
    private int emergencyPatients = 0;

    /**
     * Number of completed patients classified as non-emergencies.
     */
    private int normalPatients = 0;

    /**
     * Seed used to produce reproducible random values.
     */
    private long randomSeed = 5;

    /**
     * Random number generator used for patient conditions and routes.
     */
    private Random random = new Random(randomSeed);


    /**
     * Creates a new hospital simulation engine.
     * <p>
     * Initializes six hospital service points with their
     * capacities and service time distributions.
     * Also initializes the patient arrival process.
     *
     * @param controller the controller used to communicate
     *                   with the graphical user interface
     * @param randomSeed the seed used for random number generation
     */
    public MyEngine(IControllerMtoV controller, long randomSeed) { // NEW
        super(controller);// NEW

        this.logicController = (Controller) controller;
        this.randomSeed = randomSeed;
        this.random = new Random(randomSeed);

        servicePoints = new ServicePoint[6];

        servicePoints[0] = new ServicePoint(new Normal(10, 6, randomSeed), eventList, EventType.REGISTRATION_COMPLETE, "Registration", registrationCapacity, logicController);
        servicePoints[1] = new ServicePoint(new Normal(10, 6, randomSeed + 1), eventList, EventType.TRIAGE_COMPLETE, "Triage", triageCapacity, logicController);
        servicePoints[2] = new ServicePoint(new Normal(10, 6, randomSeed + 2), eventList, EventType.DOCTOR_COMPLETE, "Doctor", doctorCapacity, logicController);
        servicePoints[3] = new ServicePoint(new Normal(10, 6, randomSeed + 3), eventList, EventType.XRAY_COMPLETE, "X-ray", xrayCapacity, logicController);
        servicePoints[4] = new ServicePoint(new Normal(10, 6, randomSeed + 4), eventList, EventType.LABORATORY_COMPLETE, "Laboratory", laboratoryCapacity, logicController);
        servicePoints[5] = new ServicePoint(new Normal(10, 6, randomSeed + 5), eventList, EventType.TREATMENT_COMPLETE, "Treatment", treatmentCapacity, logicController);

        arrivalProcess = new ArrivalProcess(new Negexp(15, randomSeed + 6), eventList, EventType.ARRIVAL);
    }

    /**
     * Initializes the simulation by scheduling
     * the first patient arrival event.
     */
    @Override
    protected void initialization() {
        arrivalProcess.generateNext();     // First arrival in the system
    }

    /**
     * Processes a scheduled simulation event.
     * <p>
     * Handles patient arrivals, registration, triage,
     * doctor consultations, laboratory examinations,
     * X-ray examinations and treatment completion.
     * <p>
     * Updates patient queues, patient information
     * and the graphical visualization.
     *
     * @param t the simulation event to be processed
     */
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

                //Generate next arrival
                arrivalProcess.generateNext();
                break;

            case REGISTRATION_COMPLETE:
                a = servicePoints[0].removeQueue();
                servicePoints[1].addQueue(a);

                //Move patient visually to Triage
                controller.moveCustomer(a.getId(), "Triage");

                break;

            case TRIAGE_COMPLETE:
                a = servicePoints[1].removeQueue();

                //Assign patient condition during triage
                int condition = random.nextInt(100);

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

                break;

            case DOCTOR_COMPLETE:
                a = servicePoints[2].removeQueue();

                int destination = random.nextInt(100);

                if (destination < 30) {
                    // 30 %: laboratorio
                    servicePoints[4].addQueue(a);
                    controller.moveCustomer(a.getId(), "Laboratory");

                } else if (destination < 50) {
                    // 20 %: röntgen
                    servicePoints[3].addQueue(a);
                    controller.moveCustomer(a.getId(), "X-ray");

                } else if (destination < 90) {
                    // 40 %: hoito
                    servicePoints[5].addQueue(a);
                    controller.moveCustomer(a.getId(), "Treatment");

                } else {
                    // 10 %: potilas poistuu sairaalasta
                    a.setRemovalTime(Clock.getInstance().getTime());
                    a.setTotalHospitalTime();
                    String[] results = a.reportResults();
                    for (String result : results) {
                        System.out.println(result);
                    }
                    recordCompletedPatient(a);

                    controller.moveCustomer(a.getId(), "Exit");
                    controller.removeCustomer(a.getId());
                }
                break;

            case LABORATORY_COMPLETE:
                a = servicePoints[4].removeQueue();
                servicePoints[5].addQueue(a);
                controller.moveCustomer(a.getId(), "Treatment");
                break;

            case XRAY_COMPLETE:
                a = servicePoints[3].removeQueue();
                servicePoints[5].addQueue(a);
                controller.moveCustomer(a.getId(), "Treatment");
                break;

            case TREATMENT_COMPLETE:
                a = servicePoints[5].removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                a.setTotalHospitalTime();
                String[] results = a.reportResults();
                for (String result : results) {
                    System.out.println(result);
                }
                recordCompletedPatient(a);

                controller.moveCustomer(a.getId(), "Exit");
                controller.removeCustomer(a.getId());

                break;
        }
    }

    /**
     * Records a patient who has completed the hospital visit.
     * <p>
     * Updates the number of served patients, total hospital
     * time and patient priority statistics.
     * Stores the patient for later database persistence.
     *
     * @param patient the patient who has completed treatment
     *                or otherwise exited the hospital
     */
    private void recordCompletedPatient(Customer patient) {
        // Store the completed patient until we get the simulation run_id.
        completedCustomers.add(patient);

        servedPatients++;
        totalTimeInHospital += patient.getTotalHospitalTime();

        if (patient.hasPriority()) {
            emergencyPatients++;
        } else {
            normalPatients++;
        }
    }

    /**
     * Creates a statistics object for a hospital service point.
     * <p>
     * Collects waiting time, maximum queue length, capacity,
     * total service time, number of patients served and
     * utilization information.
     *
     * @param servicePoint the service point to analyze
     * @return a ServicePointStats object containing
     * the collected statistics
     */
    private ServicePointStats createStats(ServicePoint servicePoint) {
        stats = new ServicePointStats();

        stats.setServiceName(servicePoint.getName());
        stats.setAverageWaitingTime(servicePoint.getAverageWaitingTime());
        stats.setMaxQueueLength(servicePoint.getMaximumQueueLength());
        stats.setCurrentCapacity(servicePoint.getCapacity());
        stats.setTotalServiceTime(servicePoint.getTotalServiceTime());
        stats.setNumberStarted(servicePoint.getCustomersStartedService());
        stats.setUtilization(servicePoint.getUtilization());

        return stats;
    }

    /**
     * Calculates, displays and saves the simulation results.
     * <p>
     * Calculates the total number of served patients,
     * average hospital time, patient throughput and
     * service point utilization.
     * <p>
     * Identifies the service point with the highest
     * average waiting time and generates a management
     * recommendation.
     * <p>
     * Sends the results to the user interface and
     * saves simulation, patient and service point
     * results to the database.
     */
    @Override
    protected void results() {

        double simulationTime = Clock.getInstance().getTime();

        // Säilytetään yhteys GUI-käyttöliittymään.
        controller.showEndTime(simulationTime);

        System.out.println("\n========== SIMULATION RESULTS ==========");
        System.out.println("Random seed: " + randomSeed);


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

            ServicePointStats stats = createStats(point);

            System.out.println("\n" + stats.getServiceName());

            System.out.printf(
                    "Average waiting time: %.2f minutes%n",
                    stats.getAverageWaitingTime()
            );

            System.out.println(
                    "Maximum queue length: " + stats.getMaxQueueLength()
            );

            System.out.println(
                    "Current capacity: " + stats.getCurrentCapacity()
            );

            System.out.printf(
                    "Total service time: %.2f minutes%n", stats.getTotalServiceTime()
            );

            System.out.println(
                    "Patients started service: " + stats.getNumberStarted()
            );

            System.out.printf(
                    "Utilization: %.2f%%%n", stats.getUtilization()
            );
        }
        //Find the service point with the highest average waiting time
        ServicePoint bottleneck = servicePoints[0];

        for (ServicePoint point : servicePoints) {
            if (point.getAverageWaitingTime() > bottleneck.getAverageWaitingTime()) {
                bottleneck = point;
            }
        }

        //Get statistics of the bottleneck
        double averageWait = bottleneck.getAverageWaitingTime();
        int maximumQueue = bottleneck.getMaximumQueueLength();
        double utilization = bottleneck.getUtilization(simulationTime);
        int currentCapacity = bottleneck.getCapacity();

        //Suggest capacity based on waiting time and utilization
        int suggestedCapacity = currentCapacity;

        if (averageWait > 5 || utilization > 80) {
            suggestedCapacity = currentCapacity + 1;
        }

        //Create management recommendation
        String recommendation;

        if (averageWait <= 0) {
            recommendation = "No significant bottleneck detected.";
        } else if (suggestedCapacity > currentCapacity) {
            recommendation = "Increase " + bottleneck.getName()
                    + " capacity from " + currentCapacity
                    + " to " + suggestedCapacity + ".";
        } else {
            recommendation = bottleneck.getName()
                    + " has the highest average waiting time, "
                    + "but current capacity is sufficient.";
        }

        //Send management analysis results to GUI
        controller.showManagementAnalysis(
                bottleneck.getName(),
                averageWait,
                maximumQueue,
                utilization,
                currentCapacity,
                suggestedCapacity,
                recommendation
        );

        //Create DAO for saving the completed simulation run
        SimulationRunDao simulationRunDao = new SimulationRunDao();

        int runId = 0;

        try {
            runId = simulationRunDao.saveSimulationRun(
                    simulationTime,
                    randomSeed,
                    registrationCapacity,
                    triageCapacity,
                    doctorCapacity,
                    laboratoryCapacity,
                    xrayCapacity,
                    treatmentCapacity,
                    servedPatients,
                    averageHospitalTime,
                    patientsPerHour,
                    doctorUtilization,
                    bottleneck.getName()
            );

            System.out.println("\nSimulation run saved to database. run_id: " + runId);

            // Save all completed patients using this simulation's run_id.
            for (Customer customer : completedCustomers) {
                patientResultDao.savePatientResult(runId, customer);
            }

            System.out.println("Patient results saved to database.");

        } catch (SQLException e) {
            System.out.println("Failed to save simulation run to database.");
            e.printStackTrace();
        }

        for (ServicePoint point : servicePoints) {
            ServicePointStats stats = createStats(point);

            // Persist the service point stats into the database
            try {
                servicePointResultDao.persist(stats, runId);

            } catch (SQLException e) {
                System.out.println("Service point stats were not able to be persisted into the database!");

                e.printStackTrace();
            }
        }
        System.out.println("Service point results saved to database!");

        System.out.println("\n========================================");
    }
}
