package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The class that Dao uses to connect to the database.
 */
public class DatabaseConnection {

    private static Connection conn = null;

    /**
     * Is used to connect to the database.
     *
     * @return A connection object used to connect to the database.
     */
    public static Connection getConnection() {
        if (conn==null) {
            // connect if necessary
            try {
                conn = DriverManager.getConnection(
                        "jdbc:mariadb://localhost:3306/amusement_park?user=amusement&password=password");
            } catch (SQLException e) {
                System.out.println("Connection failed.");
                e.printStackTrace();
            }
            return conn;
        }
        else {
            return conn;
        }
    }

    /**
     * Terminates the connection.
     */
    public static void terminate() {
        try {
            getConnection().close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}