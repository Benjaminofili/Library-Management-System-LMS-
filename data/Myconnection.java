package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Myconnection {
    private static Myconnection instance;
    private Connection connection;
    
    // Private constructor to enforce singleton pattern
    private Myconnection() {
        initializeConnection();
    }

    public static Myconnection getInstance() {
        if (instance == null) {
            synchronized (Myconnection.class) {
                if (instance == null) {
                    instance = new Myconnection();
                }
            }
        }
        return instance;
    }

    private void initializeConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/lms_db";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established!");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Connection initialization failed:");
            e.printStackTrace();
        }
    }

    public Connection getdbconnection() throws SQLException {
        // Verify connection is still valid
        if (connection == null || connection.isClosed()) {
            initializeConnection();
        }
        return connection;
    }
}