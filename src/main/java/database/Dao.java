package database;

import distributions.Bernoulli;
import javafx.util.Pair;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;

/**
 * Dao class connects the simulator to the database.
 */
public class Dao {

    /**
     * Increments the simulation id. The increments start from 1.
     */
    public void incrementSimId(){
        Connection conn = DatabaseConnection.getConnection();
        long reallyBigNumber = System.currentTimeMillis();
        String sql = "INSERT INTO simulation (sim_id, time_stamp) VALUES (NULL, " + reallyBigNumber +");";

        try {
            Statement s = conn.createStatement();
            //ResultSet rs = s.executeQuery(sql);
            s.executeUpdate(sql);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Gets the latest simulation id from the database.
     *
     * @return The simulation id.
     */
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

    /**
     * Returns the timestamp of the simulation.
     *
     * @param id The id of the specific simulation.
     * @return The timestamp of the simulation
     */
    public Date getSimTimestampById(int id) {

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT time_stamp FROM simulation WHERE sim_id = " + id + ";";

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            rs.next();
            //Date date = rs.getTimestamp(1);
            //return rs.getTimestamp(1);
            return new Date(rs.getLong(1));
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Function made to get all simulation timestamps along with their ids.
     * @return A hashmap with the time stamps.
     */
    public Map<Integer, Long> getSimTimestamps() {

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT sim_id, time_stamp FROM simulation";
        Map<Integer, Long> list = new HashMap<>();

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while(rs.next()){
                list.put(rs.getInt(1), rs.getLong(2));
            };
            //return new Date(rs.getTimestamp(1).getTime());
            return list;
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    /*
    public LocalDateTime getSimTimeStampById(int id) {

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT time_stamp FROM simulation WHERE sim_id = " + id + ";";

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            rs.next();
            return Instant.ofEpochMilli(rs.getLong(id)).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }


     */

    /**
     * Is used to display static results.
     *
     * @param id The id of the specific results.
     * @return The static results.
     */
    public HashMap<String, Double> getStaticResultsById(int id) {

        Connection conn = DatabaseConnection.getConnection();
        HashMap<String, Double> results = new HashMap();
        String sql = "SELECT * FROM service_point WHERE sim_id = " + id + ";";


        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            rs.next();

            //System.out.println(rs.getInt(1));
            //System.out.println(rs.getDouble(2));

            double simulationTime = rs.getDouble(2);
            results.put("End time", simulationTime);
            double readyCustomers = rs.getDouble(3);
            results.put("Ready customers", readyCustomers);
            double ticketCustomers = rs.getDouble(4);
            results.put("Ticket customers", ticketCustomers);
            double wristbandCustomers = rs.getDouble(5);
            results.put("Wristband customers", wristbandCustomers);
            double unreadyCustomers = rs.getDouble(6);
            results.put("Unready customers", unreadyCustomers);
            double ticketBoothAverage = rs.getDouble(7);
            results.put("Ticket booth average", ticketBoothAverage);
            double totalTicketCount = rs.getDouble(8);
            results.put("Total ticket count", totalTicketCount);
            double wristbandAverageTime = rs.getDouble(9);
            results.put("Wristband average time", wristbandAverageTime);
            double ticketAverageTime = rs.getDouble(10);
            results.put("Ticket average time", ticketAverageTime);
            double wholeAverageTime = rs.getDouble(11);
            results.put("Whole average time", wholeAverageTime);
            double ticketWristbandTimeRatio = rs.getDouble(12);
            results.put("Wristband ticket ratio", ticketWristbandTimeRatio);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Is used to display dynamic results.
     *
     * @param id The id of the specific results.
     * @return The dynamic results.
     */
    public TreeMap<String, Double> getDynamicResultsById(int id){

        Connection conn = DatabaseConnection.getConnection();
        TreeMap<String, Double> results = new TreeMap();

        try{
            String sql = "SELECT * FROM restaurant WHERE sim_id = " + getSimId() + ";";
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            rs.next();

            double count = rs.getDouble(2);
            results.put("Restaurant count", count);
            double averageServiceTime = rs.getDouble(3);
            results.put("Restaurant average service time", averageServiceTime);
            double averageQueueTime = rs.getDouble(4);
            results.put("Restaurant average queue time", averageQueueTime);

        } catch(SQLException e){
            e.printStackTrace();
        }


        try{
            String sql = "SELECT * FROM ride WHERE sim_id = " + getSimId() + ";";
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                /*
                double rideId = rs.getDouble(2);
                results.put("Ride " + rs.getInt(2), rideId);
                double averageServiceTime = rs.getDouble(3);
                results.put("averageServiceTime", averageServiceTime);
                double averageQueueTime = rs.getDouble(4);
                results.put("averageQueueTime", averageQueueTime);
                 */
                results.put("Ride " + rs.getInt(2) + " count", (double) rs.getInt(3));
                results.put("Ride " + rs.getInt(2) + " average service time", rs.getDouble(4));
                results.put("Ride " + rs.getInt(2) + " average queue time", rs.getDouble(5));
            }

        } catch(SQLException e){
            e.printStackTrace();
        }


        try{
            String sql = "SELECT * FROM ticket_booth WHERE sim_id = " + getSimId() + ";";
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                /*
                double rideId = rs.getDouble(2);
                results.put("Ride " + rs.getInt(2), rideId);
                double averageServiceTime = rs.getDouble(3);
                results.put("averageServiceTime", averageServiceTime);
                double averageQueueTime = rs.getDouble(4);
                results.put("averageQueueTime", averageQueueTime);
                 */
                results.put("Ticket booth " + rs.getInt(2) + " count", (double) rs.getInt(3));
                results.put("Ticket booth " + rs.getInt(2) + " average service time", rs.getDouble(4));
                results.put("Ticket booth " + rs.getInt(2) + " average queue time", rs.getDouble(5));
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
        return results;
    }


    /**
     * Adds a service point to the database.
     *
     * @param simulationTime I'm not typing all this.
     * @param readyCustomers
     * @param ticketCustomers
     * @param wristbandCustomers
     * @param unreadyCustomers
     * @param ticketBoothAverage
     * @param totalTicketCount
     * @param wristbandAverageTime
     * @param ticketAverageTime
     * @param wholeAverageTime
     * @param ticketWristbandTimeRatio
     */
    public void addServicePoint(double simulationTime, int readyCustomers, int ticketCustomers, int wristbandCustomers, int unreadyCustomers, double ticketBoothAverage, int totalTicketCount, double wristbandAverageTime, double ticketAverageTime, double wholeAverageTime, double ticketWristbandTimeRatio){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO service_point (sim_id, simulation_time, ready_customers, ticket_customers, wristband_customers, unready_customers, ticket_booth_average, total_ticket_count, wristband_average_time, ticket_average_time, whole_average_time, ticket_wristband_time_ratio) VALUES (LAST_INSERT_ID(),?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, simulationTime);
            ps.setInt(2, readyCustomers);
            ps.setInt(3, ticketCustomers);
            ps.setInt(4, wristbandCustomers);
            ps.setInt(5, unreadyCustomers);
            if(ticketBoothAverage > 0) {
                ps.setDouble(6, ticketBoothAverage);

            }else{
                ps.setDouble(6, 0);
            }
            ps.setInt(7, totalTicketCount);
            ps.setDouble(8, wristbandAverageTime);
            ps.setDouble(9, ticketAverageTime);
            ps.setDouble(10, wholeAverageTime);
            ps.setDouble(11, ticketWristbandTimeRatio);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Adds a ride to the database.
     *
     * @param rideId
     * @param count
     * @param averageServeTime
     * @param averageWaitTime
     * @param variance
     * @param mean
     */
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

    /**
     * Adds a ticket booth to the database.
     *
     * @param count
     * @param ticketBoothId
     * @param averageServiceTime
     * @param averageQueueTime
     */
    public void addTicketBooth(int count, int ticketBoothId, double averageServiceTime, double averageQueueTime){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO ticket_booth (sim_id, ticket_booth_id, count, average_service_time, average_queue_time) VALUES (LAST_INSERT_ID(), ?, ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, count);
            ps.setInt(2, ticketBoothId);
            ps.setDouble(3, averageServiceTime);
            ps.setDouble(4, averageQueueTime);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Adds a restaurant to the database.
     *
     * @param count
     * @param averageServiceTime
     * @param averageQueueTime
     * @param capacity
     */
    public void addRestaurant(int count, double averageServiceTime, double averageQueueTime, int capacity){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO restaurant (sim_id, count, average_service_time, average_queue_time, capacity) VALUES (LAST_INSERT_ID(), ?, ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, count);
            ps.setDouble(2, averageServiceTime);
            ps.setDouble(3, averageQueueTime);
            ps.setInt(4, capacity);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}