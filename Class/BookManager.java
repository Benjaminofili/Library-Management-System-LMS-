/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author benja
 */
public class BookManager {

      private final Myconnection dbConnection;

    public BookManager() {
        this.dbConnection = Myconnection.getInstance();
    }
    public boolean addBook(String name, String author, double price, String isbn, String category, boolean availability) {
        try {

           Connection conn = dbConnection.getdbconnection();

            String query = "INSERT INTO books (name, author, price, isbn, category, availability) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, author);
            statement.setDouble(3, price);
            statement.setString(4, isbn);
            statement.setString(5, category);
            statement.setBoolean(6, availability);
            return statement.executeUpdate() > 0; // Returns true if a row was added
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

 

    // Method to get admin name
public String getAdminName(int userId) {
    String adminName = "Admin"; // Default fallback
    String sql = "SELECT fname FROM users WHERE id = ?";

    try (Connection conn = dbConnection.getdbconnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, userId); // Set user ID dynamically
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            adminName = rs.getString("fname");
        }

        rs.close(); // Ensure ResultSet is closed
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return adminName;
}


}
