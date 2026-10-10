package dao;

import datasource.MariaDbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for simulation run records.
 *
 * This class manages database operations for the simulation_run
 * table in the hospital patient flow simulation.
 *
 * It provides methods to save completed simulation runs and
 * retrieve previously stored simulation run records.
 *
 * Database connections are obtained through MariaDbConnection.
 */
public class SimulationRunDao {

    //SQL command for saving one completed simulation run into database
    //run_id is created automatically and run_date uses the current time
    private static final String INSERT_SIMULATION_RUN =
            "INSERT INTO simulation_run (" +
                    "simulation_time, random_seed, " +
                    "registration_capacity, triage_capacity, doctor_capacity, " +
                    "laboratory_capacity, xray_capacity, treatment_capacity, " +
                    "patients_served, average_hospital_time, patients_per_hour, " +
                    "doctor_utilization, bottleneck_service" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Saves a completed simulation run and its results to the database.
     *
     * @param simulationTime total duration of the simulation
     * @param randomSeed random seed used for reproducible results
     * @param registrationCapacity capacity of Registration
     * @param triageCapacity capacity of Triage
     * @param doctorCapacity capacity of Doctor
     * @param laboratoryCapacity capacity of Laboratory
     * @param xrayCapacity capacity of X-ray
     * @param treatmentCapacity capacity of Treatment
     * @param patientsServed number of patients served
     * @param averageHospitalTime average time patients spent in hospital
     * @param patientsPerHour patient throughput per hour
     * @param doctorUtilization doctor utilization percentage
     * @param bottleneckService name of the bottleneck service point
     * @return the automatically generated simulation run ID
     * @throws SQLException if saving the simulation run fails
     */
    public int saveSimulationRun(
            double simulationTime,
            long randomSeed,
            int registrationCapacity,
            int triageCapacity,
            int doctorCapacity,
            int laboratoryCapacity,
            int xrayCapacity,
            int treatmentCapacity,
            int patientsServed,
            double averageHospitalTime,
            double patientsPerHour,
            double doctorUtilization,
            String bottleneckService) throws SQLException {

        //Open database connection and prepare the INSERT statement
        try (Connection connection = MariaDbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_SIMULATION_RUN,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            //SET simulation input values
            statement.setDouble(1, simulationTime);
            statement.setLong(2, randomSeed);

            statement.setInt(3, registrationCapacity);
            statement.setInt(4, triageCapacity);
            statement.setInt(5, doctorCapacity);
            statement.setInt(6, laboratoryCapacity);
            statement.setInt(7, xrayCapacity);
            statement.setInt(8, treatmentCapacity);

            //Set simulation result values
            statement.setInt(9, patientsServed);
            statement.setDouble(10, averageHospitalTime);
            statement.setDouble(11, patientsPerHour);
            statement.setDouble(12, doctorUtilization);
            statement.setString(13, bottleneckService);

            //Execute the INSERT statement
            statement.executeUpdate();

            //Get the automatically generated run_id
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            //If no run_id was returned, something went wrong
            throw new SQLException("Creating simulation run failed, no run_id returned.");
        }
    }

    /**
     * Retrieves all stored simulation runs from the database.
     *
     * Each simulation run is represented as a map containing
     * its input parameters and calculated results.
     *
     * @return a list of maps containing simulation run records
     * @throws SQLException if a database connection or query operation fails
     */
    public List<Map<String, Object>> getAllSimulationRuns() throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();

        Connection conn = MariaDbConnection.getConnection();
        String sql = "SELECT * FROM simulation_run";

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();

                row.put("runId", rs.getInt(1));
                row.put("runDate", rs.getTimestamp(2));
                row.put("simulationTime", rs.getDouble(3));
                row.put("randomSeed", rs.getLong(4));
                row.put("registrationCapacity", rs.getInt(5));
                row.put("triageCapacity", rs.getInt(6));
                row.put("doctorCapacity", rs.getInt(7));
                row.put("labCapacity", rs.getInt(8));
                row.put("xrayCapacity", rs.getInt(9));
                row.put("treatmentCapacity", rs.getInt(10));
                row.put("patientsServed", rs.getInt(11));
                row.put("averageHospitalTime", rs.getDouble(12));
                row.put("patientsPerHour", rs.getDouble(13));
                row.put("doctorUtilization", rs.getDouble(14));
                row.put("bottleneckService", rs.getString(15));

                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}
