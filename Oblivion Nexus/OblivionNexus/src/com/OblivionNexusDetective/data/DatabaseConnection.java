package com.OblivionNexusDetective.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/oblivionnexus_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Bubblebek_Louw@15232211";

    // Static initializer to load the driver once
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL driver not found: " + e.getMessage());
            throw new ExceptionInInitializerError("MySQL JDBC Driver not found");
        }
    }

    // Method to open a connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }
    
    // Method to test the connection
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
