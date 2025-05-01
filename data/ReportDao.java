/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author benja
 */
public class ReportDao {
    
     private final Myconnection dbConnection;

    public ReportDao() {
        this.dbConnection =  Myconnection.getInstance();
    }

    // Data class for statistics cards
    public static class StatisticsData {
        private final int totalBooks;
        private final int booksBorrowed;
        private final int registeredReaders;
        private final int overdueBooks;

        public StatisticsData(int totalBooks, int booksBorrowed, int registeredReaders, int overdueBooks) {
            this.totalBooks = totalBooks;
            this.booksBorrowed = booksBorrowed;
            this.registeredReaders = registeredReaders;
            this.overdueBooks = overdueBooks;
        }

        public int getTotalBooks() {
            return totalBooks;
        }

        public int getBooksBorrowed() {
            return booksBorrowed;
        }

        public int getRegisteredReaders() {
            return registeredReaders;
        }

        public int getOverdueBooks() {
            return overdueBooks;
        }
    }

    // Data class for transactions (table)
    public static class Transaction {
        private String bookTitle;
        private String borrowerName;
        private String borrowedDate;
        private String dueDate;
        private String status;
        private int transactionId;

        public Transaction(String bookTitle, String borrowerName, String borrowedDate, String dueDate, String status, int transactionId) {
            this.bookTitle = bookTitle;
            this.borrowerName = borrowerName;
            this.borrowedDate = borrowedDate;
            this.dueDate = dueDate;
            this.status = status;
            this.transactionId = transactionId;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public String getBorrowerName() {
            return borrowerName;
        }

        public String getBorrowedDate() {
            return borrowedDate;
        }

        public String getDueDate() {
            return dueDate;
        }

        public String getStatus() {
            return status;
        }

        public int getTransactionId() {
            return transactionId;
        }
    }

    // Retrieve statistics for the cards
    public StatisticsData getStatisticsData() {
        int totalBooks = 0;
        int booksBorrowed = 0;
        int registeredReaders = 0;
        int overdueBooks = 0;

        try (Connection conn = dbConnection.getdbconnection()) {
            // Total Books
            String totalBooksQuery = "SELECT COUNT(*) FROM books";
            try (PreparedStatement pstmt = conn.prepareStatement(totalBooksQuery);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalBooks = rs.getInt(1);
                }
            }

            // Books Borrowed (currently borrowed, not returned)
            String booksBorrowedQuery = "SELECT COUNT(*) FROM borrowed_books WHERE returned = 0";
            try (PreparedStatement pstmt = conn.prepareStatement(booksBorrowedQuery);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    booksBorrowed = rs.getInt(1);
                }
            }

            // Registered Readers
            String registeredReadersQuery = "SELECT COUNT(*) FROM members";
            try (PreparedStatement pstmt = conn.prepareStatement(registeredReadersQuery);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    registeredReaders = rs.getInt(1);
                }
            }

            // Overdue Books
            String overdueBooksQuery = "SELECT COUNT(*) FROM borrowed_books WHERE returned = 0 AND expected_return_date < CURDATE()";
            try (PreparedStatement pstmt = conn.prepareStatement(overdueBooksQuery);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    overdueBooks = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new StatisticsData(totalBooks, booksBorrowed, registeredReaders, overdueBooks);
    }

    // Retrieve transaction data for the table
    public ObservableList<Transaction> getTransactionData() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        String query = "SELECT bb.borrow_id, bb.book_id, bb.member_id, bb.borrow_date, bb.expected_return_date, bb.returned, " +
                      "b.name AS book_title, CONCAT(m.first_name, ' ', m.last_name) AS borrower_name " +
                      "FROM borrowed_books bb " +
                      "JOIN books b ON bb.book_id = b.id " +
                      "JOIN members m ON bb.member_id = m.member_id";

        try (Connection conn = dbConnection.getdbconnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            while (rs.next()) {
                String bookTitle = rs.getString("book_title");
                String borrowerName = rs.getString("borrower_name");
                String borrowedDate = rs.getTimestamp("borrow_date").toLocalDateTime().format(formatter);
                String dueDate = rs.getDate("expected_return_date") != null ? 
                                 rs.getDate("expected_return_date").toLocalDate().format(formatter) : "N/A";
                boolean returned = rs.getBoolean("returned");
                LocalDate due = rs.getDate("expected_return_date") != null ? 
                                rs.getDate("expected_return_date").toLocalDate() : LocalDate.now();
                String status = returned ? "Returned" : 
                                (due.isBefore(LocalDate.now()) ? "Overdue" : "Borrowed");
                int transactionId = rs.getInt("borrow_id"); // Use borrow_id instead of book_id

                transactions.add(new Transaction(bookTitle, borrowerName, borrowedDate, dueDate, status, transactionId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

   // Retrieve data for the pie chart (top 5 categories by book count)
    public ObservableList<PieChart.Data> getCategoryDistributionData() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        String query = "SELECT category, COUNT(*) as count FROM books " +
                      "GROUP BY category " +
                      "ORDER BY count DESC " +
                      "LIMIT 5"; // Limit to top 5 categories

        try (Connection conn = dbConnection.getdbconnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                int count = rs.getInt("count");
                pieChartData.add(new PieChart.Data(category != null ? category : "Uncategorized", count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pieChartData;
    }

      // Retrieve data for the line chart (borrowing trends for the last month, broken down by day)
  public ObservableList<XYChart.Series<String, Number>> getBorrowingTrendsData() {
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Books Borrowed");

    String query = "SELECT DATE_SUB(borrow_date, INTERVAL WEEKDAY(borrow_date) DAY) AS week_start, COUNT(*) as count " +
                  "FROM borrowed_books " +
                  "WHERE borrow_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) " +
                  "GROUP BY week_start " +
                  "ORDER BY week_start";

    try (Connection conn = dbConnection.getdbconnection();
         PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
        while (rs.next()) {
            LocalDate weekStart = rs.getDate("week_start").toLocalDate();
            int count = rs.getInt("count");
            LocalDate weekEnd = weekStart.plusDays(6);
            String label = weekStart.format(formatter) + " - " + weekEnd.format(formatter);
            series.getData().add(new XYChart.Data<>(label, count));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return FXCollections.observableArrayList(series);
}
    
}
