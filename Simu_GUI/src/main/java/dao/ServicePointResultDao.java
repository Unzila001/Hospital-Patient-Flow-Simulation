package dao;

import datasource.MariaDbConnection;
import simu.model.ServicePoint;
import simu.model.ServicePointStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServicePointResultDao {

    public void persist (ServicePointStats servicePointStats, ServicePoint servicePoint) throws SQLException {
        Connection conn = MariaDbConnection.getConnection();
        String sql = "INSERT INTO Service_point_result (average_waiting_time, max_queue_length, total_service_time, patients_started_service, utilization, service_id) VALUES (?, ?, ?, ?, ?, ?)";

        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, servicePointStats.getAverageWaitingTime());
            ps.setInt(2, servicePointStats.getMaxQueueLength());
            ps.setDouble(3, servicePointStats.getTotalServiceTime());
            ps.setInt(4, servicePointStats.getNumberStarted());
            ps.setDouble(5, servicePointStats.getUtilization());
            ps.setInt(6, servicePoint.getId());

            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
