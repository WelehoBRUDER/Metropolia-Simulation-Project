package database;

import java.sql.*;
import java.util.*;

public class Dao {
    public void testDao() {
        System.out.println("SEE MEE 1");

        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM restaurant";

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            System.out.println("SEE MEE 2");


            while (rs.next()) {
                /*
                String firstName = rs.getString(1);
                String lastName = rs.getString(2);
                String email = rs.getString(3);
                double salary = rs.getDouble(4);

                 */
                System.out.println("SEE MEE 3");
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //return employees;
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

    public void addRestaurant(){
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO restaurant (sim_id, customer_amount, average_serve_time, average_wait_time) VALUES (LAST_INSERT_ID(), ?, ?, ?);";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ps.setInt(3, 1);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
