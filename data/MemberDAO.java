/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import model.Member;
import model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benja
 */
public class MemberDAO {

    // Inserts a new member into the database.
    public static void addMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (first_name, last_name, email, phone, address) VALUES (?, ?, ?, ?, ?)";
        Myconnection db = Myconnection.getInstance();
        Connection con = db.getdbconnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, member.getFirstName());
            ps.setString(2, member.getLastName());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPhone());
            ps.setString(5, member.getAddress());
            ps.executeUpdate();
        }
    }

    // Updates an existing member in the database.
    public static void updateMember(Member member) throws SQLException {
        String sql = "UPDATE members SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ? WHERE member_id = ?";
        Myconnection db = Myconnection.getInstance();
        Connection con = db.getdbconnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, member.getFirstName());
            ps.setString(2, member.getLastName());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPhone());
            ps.setString(5, member.getAddress());
            ps.setInt(6, member.getMemberId());
            ps.executeUpdate();
        }
    }

    // Deletes a member from the database.
    public static void deleteMember(int memberId) throws SQLException {
        String sql = "DELETE FROM members WHERE member_id = ?";
        Myconnection db = Myconnection.getInstance();
        Connection con = db.getdbconnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.executeUpdate();
        }
    }

    // Loads all members from the database.
    public static ObservableList<Member> getAllMembers() {
        ObservableList<Member> memberList = FXCollections.observableArrayList();
        String sql = "SELECT member_id, first_name, last_name, email, phone, address FROM members";
        try {
            Myconnection db = Myconnection.getInstance();
            Connection con = db.getdbconnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getInt("member_id"));
                member.setFirstName(rs.getString("first_name"));
                member.setLastName(rs.getString("last_name"));
                member.setEmail(rs.getString("email"));
                member.setPhone(rs.getString("phone"));
                member.setAddress(rs.getString("address"));
                memberList.add(member);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Optionally log the error or rethrow a custom exception.
        }
        return memberList;
    }

    public static ObservableList<User> getPendingLibrarians() {
        ObservableList<User> pendingList = FXCollections.observableArrayList();
        String sql = "SELECT id, fname, lname, email, approved FROM users WHERE role = 'Librarian' AND approved = FALSE";

        try {
            Myconnection db = Myconnection.getInstance();
            Connection con = db.getdbconnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                pendingList.add(new User(
                        rs.getInt("id"),
                        rs.getString("fname"),
                        rs.getString("lname"),
                        rs.getString("email"),
                        rs.getBoolean("approved")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendingList;
    }

    public static boolean approveLibrarian(int userId) {
        String sql = "UPDATE users SET approved = TRUE WHERE id = ?";

        try {
            Myconnection db = Myconnection.getInstance();
            Connection con = db.getdbconnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0; // Returns true if update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Returns false if an error occurs
        }
    }

    public static boolean rejectLibrarian(int userId) {
        String sql = "DELETE FROM users WHERE id = ? AND approved = FALSE"; // ✅ Only delete unapproved accounts

        try {
            Myconnection db = Myconnection.getInstance();
            Connection con = db.getdbconnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
