package mySQLdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static final String URL = "jdbc:mysql://mysql-key.alwaysdata.net/key_ecommerce";
    private static final String USERNAME = "key";
    private static final String PASSWORD = "mshibakezibri";
    private static Connection connection;

    // Private constructor to prevent instantiation
    private DBConnect() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            throw new SQLException("Unable to load JDBC Driver.", e);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
            throw new SQLException("Unable to establish database connection.", e);
        }
    }

    // Static method to get connection instance
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            new DBConnect(); // Establish the connection if it's not already established
        }
        return connection;
    }

    // Close the connection if needed
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed.");
        }
    }
}
