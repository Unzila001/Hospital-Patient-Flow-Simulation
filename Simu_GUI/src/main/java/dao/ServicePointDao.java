package dao;

import datasource.MariaDbConnection;
import simu.model.ServicePoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicePointDao {

    public int getId(String service_name) throws SQLException {
        Connection conn = MariaDbConnection.getConnection();
        String sql = "SELECT id FROM Service_point WHERE service_name = ?";

        int id = 0;

        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, service_name);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                id = rs.getInt(1);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return id;
    }

}
