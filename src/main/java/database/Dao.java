package database;

import java.sql.*;
import java.util.*;


class ServicePoint {
    private int simId;
    private String servicePointType;
    private int count;
    private double averageServiceTime;
    private double averageQueueTime;
    private int ticketsBought;

    public ServicePoint(int simId, String servicePointType, int count, double averageServiceTime, double averageQueueTime, int ticketsBought) {
        this.simId = simId;
        this.servicePointType = servicePointType;
        this.count = count;
        this.averageServiceTime = averageServiceTime;
        this.averageQueueTime = averageQueueTime;
        this.ticketsBought = ticketsBought;
    }

    public int getSimId() {
        return simId;
    }

    public void setSimId(int simId) {
        this.simId = simId;
    }

    public String getServicePointType() {
        return servicePointType;
    }

    public void setServicePointType(String servicePointType) {
        this.servicePointType = servicePointType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(double averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public double getAverageQueueTime() {
        return averageQueueTime;
    }

    public void setAverageQueueTime(double averageQueueTime) {
        this.averageQueueTime = averageQueueTime;
    }

    public int getTicketsBought() {
        return ticketsBought;
    }

    public void setTicketsBought(int ticketsBought) {
        this.ticketsBought = ticketsBought;
    }
}

class Ride {
    private int simId;
    private int rideId;
    private int count;
    private double averageServiceTime;
    private double averageQueueTime;

    public Ride(int simId, int rideId, int count, double averageServiceTime, double averageQueueTime) {
        this.simId = simId;
        this.rideId = rideId;
        this.count = count;
        this.averageServiceTime = averageServiceTime;
        this.averageQueueTime = averageQueueTime;
    }

    public int getSimId() {
        return simId;
    }

    public void setSimId(int simId) {
        this.simId = simId;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(double averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public double getAverageQueueTime() {
        return averageQueueTime;
    }

    public void setAverageQueueTime(double averageQueueTime) {
        this.averageQueueTime = averageQueueTime;
    }
}

class TicketBooth {
    private int simId;
    private int count;
    private double averageServiceTime;
    private double averageQueueTime;

    public TicketBooth(int simId, int count, double averageServiceTime, double averageQueueTime) {
        this.simId = simId;
        this.count = count;
        this.averageServiceTime = averageServiceTime;
        this.averageQueueTime = averageQueueTime;
    }

    public int getSimId() {
        return simId;
    }

    public void setSimId(int simId) {
        this.simId = simId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(double averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public double getAverageQueueTime() {
        return averageQueueTime;
    }

    public void setAverageQueueTime(double averageQueueTime) {
        this.averageQueueTime = averageQueueTime;
    }
}

class Restaurant {
    private int simId;
    private int count;
    private double averageServiceTime;
    private double averageQueueTime;

    public Restaurant(int simId, int count, double averageServiceTime, double averageQueueTime) {
        this.simId = simId;
        this.count = count;
        this.averageServiceTime = averageServiceTime;
        this.averageQueueTime = averageQueueTime;
    }

    public int getSimId() {
        return simId;
    }

    public void setSimId(int simId) {
        this.simId = simId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(double averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public double getAverageQueueTime() {
        return averageQueueTime;
    }

    public void setAverageQueueTime(double averageQueueTime) {
        this.averageQueueTime = averageQueueTime;
    }
}

public class Dao {

    public void incrementSimId(){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO simulation (sim_id) VALUES (NULL);";

        try {
            Statement s = conn.createStatement();
            //ResultSet rs = s.executeQuery(sql);
            s.executeUpdate(sql);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList testDao() {

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM restaurant";
        ArrayList list = new ArrayList();

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                int simId = rs.getInt(1);
                int customerAmount = rs.getInt(2);
                double averageServeTime = rs.getDouble(3);
                double averageWaitTime = rs.getDouble(4);

                ArrayList list2 = new ArrayList();
                list2.add(simId);
                list2.add(customerAmount);
                list2.add(averageServeTime);
                list2.add(averageWaitTime);
                list.add(list2);

                //list.add(simId + customerAmount + averageServeTime + averageWaitTime);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Restaurant getRestaurantById(int id) {

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM restaurant WHERE sim_id = " + id + ";";
        //System.out.println(sql);

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                int simId = rs.getInt(1);
                int customerAmount = rs.getInt(2);
                double averageServeTime = rs.getDouble(3);
                double averageWaitTime = rs.getDouble(4);
                Restaurant Restaurant = new Restaurant(simId, customerAmount, averageServeTime, averageWaitTime);
                return Restaurant;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        //return Restaurant;
        return null;
    }

    public void addServicePoint(String servicePointType, int count, double averageServiceTime, double averageQueueTime, int ticketsBought){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO service_point (sim_id, service_point_type, count, average_service_time, average_queue_time, tickets_bought) VALUES (LAST_INSERT_ID(),?, ?, ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, servicePointType);
            ps.setInt(2, count);
            ps.setDouble(3, averageServiceTime);
            ps.setDouble(4, averageQueueTime);
            ps.setInt(5, ticketsBought);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addRide(int rideId, int count, double averageServeTime, double averageWaitTime){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO ride (sim_id, ride_id, count, average_service_time, average_queue_time) VALUES (LAST_INSERT_ID(), ?, ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, rideId);
            ps.setInt(2, count);
            ps.setDouble(3, averageServeTime);
            ps.setDouble(4, averageWaitTime);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addTicketBooth(int count, double averageServiceTime, double averageQueueTime){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO ticket_booth (sim_id, count, average_service_time, average_queue_time) VALUES (LAST_INSERT_ID(), ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, count);
            ps.setDouble(2, averageServiceTime);
            ps.setDouble(3, averageQueueTime);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addRestaurant(int count, double averageServiceTime, double averageQueueTime){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO restaurant (sim_id, count, average_service_time, average_queue_time) VALUES (LAST_INSERT_ID(), ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, count);
            ps.setDouble(2, averageServiceTime);
            ps.setDouble(3, averageQueueTime);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
}