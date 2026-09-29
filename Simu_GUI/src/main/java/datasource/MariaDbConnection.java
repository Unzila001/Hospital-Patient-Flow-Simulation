package datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDbConnection {

    private static final String URL = "jdbc:mariabb://10.120.36.52:3306/hospital_simulation";

    private static final String USER = "hospital_app";
    private static final String PASSWORD = "TVT25-0";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
