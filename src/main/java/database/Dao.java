package database;

import java.sql.*;
import java.util.*;

class restaurant{
    private int simId;
    private int customerAmount;
    private double average_serve_time;
    private double average_wait_time;

    public restaurant(int simId, int customerAmount, double average_serve_time, double average_wait_time) {
        this.simId = simId;
        this.customerAmount = customerAmount;
        this.average_serve_time = average_serve_time;
        this.average_wait_time = average_wait_time;
    }

    public int getSimId() {
        return simId;
    }

    public void setSimId(int simId) {
        this.simId = simId;
    }

    public int getCustomerAmount() {
        return customerAmount;
    }

    public void setCustomerAmount(int customerAmount) {
        this.customerAmount = customerAmount;
    }

    public double getAverage_serve_time() {
        return average_serve_time;
    }

    public void setAverage_serve_time(double average_serve_time) {
        this.average_serve_time = average_serve_time;
    }

    public double getAverage_wait_time() {
        return average_wait_time;
    }

    public void setAverage_wait_time(double average_wait_time) {
        this.average_wait_time = average_wait_time;
    }
}

class servicePoint{
    private int simId;
    private int customerAmount;
    private double average_serve_time;
    private double average_wait_time;
    private int tickets_bought;

    public servicePoint(int simId, int customerAmount, double average_serve_time, double average_wait_time, int tickets_bought) {
        this.simId = simId;
        this.customerAmount = customerAmount;
        this.average_serve_time = average_serve_time;
        this.average_wait_time = average_wait_time;
        this.tickets_bought = tickets_bought;
    }

    public int getSimId() {
        return simId;
    }

    public void setSimId(int simId) {
        this.simId = simId;
    }

    public int getCustomerAmount() {
        return customerAmount;
    }

    public void setCustomerAmount(int customerAmount) {
        this.customerAmount = customerAmount;
    }

    public double getAverage_serve_time() {
        return average_serve_time;
    }

    public void setAverage_serve_time(double average_serve_time) {
        this.average_serve_time = average_serve_time;
    }

    public double getAverage_wait_time() {
        return average_wait_time;
    }

    public void setAverage_wait_time(double average_wait_time) {
        this.average_wait_time = average_wait_time;
    }

    public int getTickets_bought() {
        return tickets_bought;
    }

    public void setTickets_bought(int tickets_bought) {
        this.tickets_bought = tickets_bought;
    }
}

class ride {
    private int simId;
    private int ride_id;
    private int customerAmount;
    private double average_serve_time;
    private double average_wait_time;

    public ride(int simId, int ride_id, int customerAmount, double average_serve_time, double average_wait_time) {
        this.simId = simId;
        this.ride_id = ride_id;
        this.customerAmount = customerAmount;
        this.average_serve_time = average_serve_time;
        this.average_wait_time = average_wait_time;
    }

    public int getSimId() {
        return simId;
    }

    public void setSimId(int simId) {
        this.simId = simId;
    }

    public int getRide_id() {
        return ride_id;
    }

    public void setRide_id(int ride_id) {
        this.ride_id = ride_id;
    }

    public int getCustomerAmount() {
        return customerAmount;
    }

    public void setCustomerAmount(int customerAmount) {
        this.customerAmount = customerAmount;
    }

    public double getAverage_serve_time() {
        return average_serve_time;
    }

    public void setAverage_serve_time(double average_serve_time) {
        this.average_serve_time = average_serve_time;
    }

    public double getAverage_wait_time() {
        return average_wait_time;
    }

    public void setAverage_wait_time(double average_wait_time) {
        this.average_wait_time = average_wait_time;
    }
}

public class Dao {
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

    public restaurant getRestaurantById(int id) {

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM restaurant WHERE sim_id = " + id + ";";
        System.out.println(sql);

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                int simId = rs.getInt(1);
                int customerAmount = rs.getInt(2);
                double averageServeTime = rs.getDouble(3);
                double averageWaitTime = rs.getDouble(4);
                restaurant Restaurant = new restaurant(simId, customerAmount, averageServeTime, averageWaitTime);
                return Restaurant;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        //return Restaurant;
        return null;
    }

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

    public void addRestaurant(int customerAmount, double averageServeTime, double averageWaitTime){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO restaurant (sim_id, customer_amount, average_serve_time, average_wait_time) VALUES (LAST_INSERT_ID(), ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerAmount);
            ps.setDouble(2, averageServeTime);
            ps.setDouble(3, averageWaitTime);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addServicePoint(int customerAmount, double averageServeTime, double averageWaitTime, int ticketsBought){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO service_point (sim_id, customer_amount, average_serve_time, average_wait_time, tickets_bought) VALUES (LAST_INSERT_ID(), ?, ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerAmount);
            ps.setDouble(2, averageServeTime);
            ps.setDouble(3, averageWaitTime);
            ps.setInt(4, ticketsBought);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addRide(int customerAmount, double averageServeTime, double averageWaitTime){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO ride (sim_id, customer_amount, average_serve_time, average_wait_time) VALUES (LAST_INSERT_ID(), ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerAmount);
            ps.setDouble(2, averageServeTime);
            ps.setDouble(3, averageWaitTime);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
