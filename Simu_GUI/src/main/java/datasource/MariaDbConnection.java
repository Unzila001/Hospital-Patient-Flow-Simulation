package datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDbConnection {

    private static final String URL =
            "jdbc:mariadb://10.120.36.52:3306/hospital_simulation";

    private static final String USER = "hospital_app";
    private static final String PASSWORD = "TVT25-O";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {

        try (Connection connection = getConnection()) {

            System.out.println("Database connection successful!");
            System.out.println("Connected to: " + connection.getCatalog());

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
