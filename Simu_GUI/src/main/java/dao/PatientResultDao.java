package dao;

import datasource.MariaDbConnection;
import simu.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PatientResultDao {

    // SQL command for saving one completed patient into the patient_result table.
    //
    // The question marks (?) are placeholders.
    // The actual patient values are added later in savePatientResult().
    //
    // patient_result_id is NOT included here because the database
    // creates it automatically with AUTO_INCREMENT.
    private static final String INSERT_PATIENT_RESULT =
            "INSERT INTO patient_result (" +
                    "run_id, patient_id, patient_condition, priority, " +
                    "arrival_time, removal_time, total_hospital_time" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?)";


    // Saves one completed patient into the database.
    //
    // runId tells which simulation run this patient belongs to.
    // customer contains the information about the completed patient.
    public void savePatientResult(int runId, Customer customer) throws SQLException {

        // Open a connection to MariaDB.
        //
        // PreparedStatement prepares our INSERT command.
        // try-with-resources automatically closes the connection
        // and statement when this block is finished.
        try (Connection connection = MariaDbConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(INSERT_PATIENT_RESULT)) {

            // Replace the first ? with the simulation run ID.
            statement.setInt(1, runId);

            // Replace the second ? with the patient's ID.
            statement.setInt(2, customer.getId());

            // Replace the third ? with the patient's condition,
            // for example "illness", "minor injury" or "serious injury".
            statement.setString(3, customer.getPatientCondition());

            // Replace the fourth ? with the patient's priority.
            // true = priority patient
            // false = normal priority
            statement.setBoolean(4, customer.hasPriority());

            // Replace the fifth ? with the time when the patient arrived.
            statement.setDouble(5, customer.getArrivalTime());

            // Replace the sixth ? with the time when the patient left the hospital.
            statement.setDouble(6, customer.getRemovalTime());

            // Replace the seventh ? with the patient's total time in the hospital.
            statement.setDouble(7, customer.getTotalHospitalTime());

            // Execute the INSERT command.
            // This is the point where the patient is actually saved into MariaDB.
            statement.executeUpdate();
        }
    }
}