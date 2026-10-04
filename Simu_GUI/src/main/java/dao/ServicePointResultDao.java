package dao;

import datasource.MariaDbConnection;
import simu.model.ServicePoint;
import simu.model.ServicePointStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ServicePointResultDao {

    ServicePointDao dao = new ServicePointDao();

    public void persist (ServicePointStats servicePointStats, int runId) throws SQLException {
        Connection conn = MariaDbConnection.getConnection();
        String sql = "INSERT INTO Service_point_result (average_waiting_time, max_queue_length, total_service_time, patients_started_service, utilization, service_id, run_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, servicePointStats.getAverageWaitingTime());
            ps.setInt(2, servicePointStats.getMaxQueueLength());
            ps.setDouble(3, servicePointStats.getTotalServiceTime());
            ps.setInt(4, servicePointStats.getNumberStarted());
            ps.setDouble(5, servicePointStats.getUtilization());
            ps.setInt(6, dao.getId(servicePointStats.getServiceName()));
            ps.setInt(7, runId);

            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<ServicePointStats> fetchServicePointResult(int runId) throws SQLException {
        Connection conn = MariaDbConnection.getConnection();
        String sql = "SELECT service_id, average_waiting_time, max_queue_length, total_service_time, patients_started_service, utilization FROM Service_point_result WHERE run_id = ?";

        ArrayList<ServicePointStats> results = new ArrayList<>();
        HashMap<String, Integer> servicePointValues = new HashMap<>();
        int serviceId = 0;
        double averageWaitingTime = 0;
        int max_queue_length = 0;
        double total_service_time = 0;
        int patients_starter = 0;
        double utilization = 0;

        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, runId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                serviceId = rs.getInt(1);
                averageWaitingTime = rs.getDouble(2);
                max_queue_length = rs.getInt(3);
                total_service_time = rs.getDouble(4);
                patients_starter = rs.getInt(5);
                utilization = rs.getDouble(6);

                ServicePointDao servicePointDao = new ServicePointDao();

                servicePointValues = servicePointDao.getServicePointData(serviceId);

                String name = servicePointValues.keySet().iterator().next();

                ServicePointStats servicePointStats = new ServicePointStats(name, averageWaitingTime, max_queue_length, servicePointValues.get(name), total_service_time, patients_starter, utilization);

                results.add(servicePointStats);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return results;
    }
}
