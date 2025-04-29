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
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benja
 */
public class BookDAO {

    private final Myconnection dbConnection;

    public BookDAO() {
        this.dbConnection = Myconnection.getInstance();
    }

    // Create
    public int addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (name, author, price, isbn, category, quantity, availability) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getdbconnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters with validation
            pstmt.setString(1, book.getName().trim());
            pstmt.setString(2, book.getAuthor().trim());
            pstmt.setDouble(3, book.getPrice());
            pstmt.setString(4, ISBNValidator.cleanISBN(book.getIsbn()));
            pstmt.setString(5, book.getCategory().trim());
            pstmt.setInt(6, book.getQuantity());
            pstmt.setBoolean(7, book.isAvailability());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                throw new SQLException("Creating book failed, no ID obtained");
            }
        }
    }

    // Read
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, name, author, price, isbn, category,availability,quantity FROM books"; // Include "price"

        try (Connection conn = dbConnection.getdbconnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getDouble("price"), // Retrieve price
                        rs.getString("isbn"),
                        rs.getString("category"),
                        rs.getBoolean("availability"),
                        rs.getInt("quantity")
                );
                books.add(book);
            }
        }
        return books;
    }

    // Update
    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET name=?, author=?, price=?, isbn=?, category=?, quantity=?, availability=? "
                + "WHERE id=?";

        try (Connection conn = dbConnection.getdbconnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getName());
            pstmt.setString(2, book.getAuthor());
            pstmt.setDouble(3, book.getPrice());
            pstmt.setString(4, book.getIsbn());
            pstmt.setString(5, book.getCategory());
            pstmt.setInt(6, book.getQuantity());
            pstmt.setBoolean(7, book.isAvailability());
            pstmt.setInt(8, book.getId());

            pstmt.executeUpdate();
        }
    }

    // Delete
    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = dbConnection.getdbconnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Book> getBooks(int page, int pageSize) throws SQLException {
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM books LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getdbconnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pageSize);
            pstmt.setInt(2, offset);

            ResultSet rs = pstmt.executeQuery();
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                // Create Book objects from result set
            }
            return books;
        }
    }

    // Get total book count
    public int getTotalBooks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM books";
        try (Connection conn = dbConnection.getdbconnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM books WHERE category IS NOT NULL";

        try (Connection conn = dbConnection.getdbconnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return categories;
    }

    public boolean borrowBook(int bookId, String memberId, LocalDateTime borrowDateTime, LocalDate returnDate) {
        try (Connection conn = dbConnection.getdbconnection()) {
            // Reduce book quantity and update availability
            String updateBookSql = "UPDATE books SET quantity = quantity - 1, availability = CASE WHEN quantity = 0 THEN FALSE ELSE availability END WHERE id = ?";
            try (PreparedStatement bookStmt = conn.prepareStatement(updateBookSql)) {
                bookStmt.setInt(1, bookId);
                int affectedRows = bookStmt.executeUpdate();
                if (affectedRows == 0) {
                    return false; // No book was found or available
                }
            }

            // Insert borrowing record, including expected return date
            String insertBorrowSql = "INSERT INTO borrowed_books (book_id, member_id, borrow_date, expected_return_date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement borrowStmt = conn.prepareStatement(insertBorrowSql)) {
                borrowStmt.setInt(1, bookId);
                borrowStmt.setString(2, memberId);
                borrowStmt.setTimestamp(3, Timestamp.valueOf(borrowDateTime)); // Timestamp for borrow date
                borrowStmt.setDate(4, java.sql.Date.valueOf(returnDate)); // User-selected return date
                borrowStmt.executeUpdate();
            }

            return true; // Book successfully borrowed
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Borrowing failed due to an error
        }
    }

  public boolean returnBook(int bookId, String memberId, LocalDate returnDate) {
    try (Connection conn = dbConnection.getdbconnection()) {
        // Ensure book was borrowed by this member
        String checkBorrowSql = "SELECT * FROM borrowed_books WHERE book_id = ? AND member_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkBorrowSql)) {
            checkStmt.setInt(1, bookId);
            checkStmt.setString(2, memberId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                return false; // No matching borrow record found
            }
        }

        // Update book quantity back in the inventory
        String updateBookSql = "UPDATE books SET quantity = quantity + 1, availability = TRUE WHERE id = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateBookSql)) {
            updateStmt.setInt(1, bookId);
            updateStmt.executeUpdate();
        }

        // Update the return_date column instead of deleting the record
        String updateBorrowSql = "UPDATE borrowed_books SET return_date = ?, returned = TRUE WHERE book_id = ? AND member_id = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateBorrowSql)) {
            updateStmt.setDate(1, java.sql.Date.valueOf(returnDate));
            updateStmt.setInt(2, bookId);
            updateStmt.setString(3, memberId);
            updateStmt.executeUpdate();
        }

        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public int getTotalBooksBorrowed() {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM borrowed_books";

        try (Connection conn = dbConnection.getdbconnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public List<String> getMostBorrowedBooks() {
        List<String> mostBorrowed = new ArrayList<>();
        String sql = "SELECT b.name, COUNT(bb.book_id) AS borrow_count FROM borrowed_books bb "
                + "JOIN books b ON bb.book_id = b.id GROUP BY bb.book_id ORDER BY borrow_count DESC LIMIT 5";

        try (Connection conn = dbConnection.getdbconnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                mostBorrowed.add(rs.getString("name") + " (Borrowed " + rs.getInt("borrow_count") + " times)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mostBorrowed;
    }

    public List<String> getMostActiveMembers() {
        List<String> activeMembers = new ArrayList<>();
        String sql = "SELECT member_id, COUNT(member_id) AS borrow_count FROM borrowed_books "
                + "GROUP BY member_id ORDER BY borrow_count DESC LIMIT 5";

        try (Connection conn = dbConnection.getdbconnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                activeMembers.add("Member ID: " + rs.getString("member_id") + " (Borrowed " + rs.getInt("borrow_count") + " books)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activeMembers;
    }

}
