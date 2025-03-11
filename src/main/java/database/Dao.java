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
    private double variance;
    private double mean;

    public Ride(int simId, int rideId, int count, double averageServiceTime, double averageQueueTime, double variance, double mean) {
        this.simId = simId;
        this.rideId = rideId;
        this.count = count;
        this.averageServiceTime = averageServiceTime;
        this.averageQueueTime = averageQueueTime;
        this.variance = variance;
        this.mean = mean;
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

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
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
    private int capacity;

    public Restaurant(int simId, int count, double averageServiceTime, double averageQueueTime, int capacity) {
        this.simId = simId;
        this.count = count;
        this.averageServiceTime = averageServiceTime;
        this.averageQueueTime = averageQueueTime;
        this.capacity = capacity;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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

    public int getSimId() {

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT MAX(sim_id) FROM simulation";

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            rs.next();
            return rs.getInt(1);
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public ArrayList<Ride> getRideById(int id) {

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM ride WHERE sim_id = " + id + ";";
        ArrayList<Ride> list = new ArrayList();

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                int simId = rs.getInt(1);
                int rideId = rs.getInt(2);
                int count = rs.getInt(3);
                double averageServiceTime = rs.getDouble(4);
                double averageQueueTime = rs.getDouble(5);
                double variance = rs.getDouble(6);
                double mean = rs.getDouble(7);
                Ride ride = new Ride(simId, rideId, count, averageServiceTime, averageQueueTime, variance, mean);
                list.add(ride);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<Restaurant> getRestaurantById(int id) {

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM restaurant WHERE sim_id = " + id + ";";
        ArrayList<Restaurant> list = new ArrayList();

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                int simId = rs.getInt(1);
                int count = rs.getInt(2);
                double averageServiceTime = rs.getDouble(3);
                double averageQueueTime = rs.getDouble(4);
                int capacity = rs.getInt(5);
                Restaurant restaurant = new Restaurant(simId, count, averageServiceTime, averageQueueTime, capacity);
                list.add(restaurant);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
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

    public void addRide(int rideId, int count, double averageServeTime, double averageWaitTime, double variance, double mean){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO ride (sim_id, ride_id, count, average_service_time, average_queue_time, variance, mean) VALUES (LAST_INSERT_ID(), ?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, rideId);
            ps.setInt(2, count);
            ps.setDouble(3, averageServeTime);
            ps.setDouble(4, averageWaitTime);
            ps.setDouble(5, variance);
            ps.setDouble(6, mean);
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