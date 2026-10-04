import dao.ServicePointResultDao;
import dao.SimulationRunDao;
import simu.model.ServicePointStats;
import view.SimulatorGUI;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* to install JavaFX (adapted from https://openjfx.io/openjfx-docs/):
 *	- Open Module Settings
 * 		- Libraries, add
 *			- from Maven openjfx.javafx.fxml:19.0.2 (this includes all necessary other JavaFX libraries)
 *	- Run Configuration Edit
 *		- Modify Options/Add VM options
 *			- --module-path "lib" --add-modules javafx.controls,javafx.fxml
 */

public class Main { // Simulator using Java FX
	public static void main(String args[]) {
		//SimulatorGUI.main(args);

		ServicePointResultDao servicePointResultDao = new ServicePointResultDao();

		/*
		try {
			ArrayList<ServicePointStats> values = new ArrayList<>();
			values = servicePointResultDao.fetchServicePointResult(7);

			for (ServicePointStats stats : values) {
				System.out.println("Name: " + stats.getServiceName());
				System.out.println("Average waiting time: " + stats.getAverageWaitingTime());
				System.out.println("Max queue length: " + stats.getMaxQueueLength());
				System.out.println("Service point value: " + stats.getCurrentCapacity());
				System.out.println("Total service time: " + stats.getTotalServiceTime());
				System.out.println("Patients started: " + stats.getNumberStarted());
				System.out.println("Utilization: " + stats.getUtilization());
				System.out.println("-----------------------------");
			}
		} catch (SQLException e){
			e.printStackTrace();
		}

		try {
			SimulationRunDao simulationRunDao = new SimulationRunDao();

			List<Map<String, Object>> values = simulationRunDao.getAllSimulationRuns();

			for (Map<String, Object> row : values) {
				System.out.println("Run ID: " + row.get("runId"));
				System.out.println("Run Date: " + row.get("runDate"));
				System.out.println("Simulation Time: " + row.get("simulationTime"));
				System.out.println("Random Seed: " + row.get("randomSeed"));
				System.out.println("Registration Capacity: " + row.get("registrationCapacity"));
				System.out.println("Triage Capacity: " + row.get("triageCapacity"));
				System.out.println("Doctor Capacity: " + row.get("doctorCapacity"));
				System.out.println("Laboratory Capacity: " + row.get("laboratoryCapacity"));
				System.out.println("X-ray Capacity: " + row.get("xrayCapacity"));
				System.out.println("Treatment Capacity: " + row.get("treatmentCapacity"));
				System.out.println("Patients Served: " + row.get("patientsServed"));
				System.out.println("Average Hospital Time: " + row.get("averageHospitalTime"));
				System.out.println("Patients Per Hour: " + row.get("patientsPerHour"));
				System.out.println("Doctor Utilization: " + row.get("doctorUtilization"));
				System.out.println("Bottleneck Service: " + row.get("bottleneckService"));
				System.out.println("-----------------------------");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		 */
	}
}
