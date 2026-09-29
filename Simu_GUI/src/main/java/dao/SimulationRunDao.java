package dao;

import datasource.MariaDbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    //Save one completed simulation run and return its generated run_id
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
}
