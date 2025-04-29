/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import Class.Myconnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author benja
 */
public class BorrowBookDialogController implements Initializable {

  
    private final Myconnection dbConnection;
    
        public BorrowBookDialogController() {
        this.dbConnection = Myconnection.getInstance();
    }
    @FXML private ComboBox<String> bookComboBox;
    @FXML private TextField memberIdField;
    @FXML private DatePicker borrowDatePicker;
    @FXML private DatePicker returnDatePicker;

    private boolean confirmed = false;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
          DialogPane dialogPane = (DialogPane) borrowDatePicker.getScene().getRoot();

        // Create button types in controller
        ButtonType borrowButton = new ButtonType("Borrow", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialogPane.getButtonTypes().addAll(cancelButton, borrowButton);

        // Set default borrow date to today
        borrowDatePicker.setValue(LocalDate.now());
    }    
    
    // Method to confirm borrowing
    public boolean isConfirmed() {
        return confirmed;
    }

    // Handle the borrowing process
    public void handleBorrow() {
        String bookTitle = bookComboBox.getValue();
        String memberId = memberIdField.getText();
        LocalDate borrowDate = borrowDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (bookTitle == null || memberId.isEmpty() || returnDate == null) {
            showErrorAlert("Invalid Input", "All fields must be filled before proceeding!");
            return;
        }

        try (Connection conn = dbConnection.getdbconnection()) {
            // Reduce book quantity
            String updateBookSql = "UPDATE books SET quantity = quantity - 1, availability = CASE WHEN quantity = 0 THEN FALSE ELSE availability END WHERE title = ?";
            PreparedStatement bookStmt = conn.prepareStatement(updateBookSql);
            bookStmt.setString(1, bookTitle);
            bookStmt.executeUpdate();

            // Insert borrowing record
            String insertBorrowSql = "INSERT INTO borrowed_books (book_title, member_id, borrow_date, return_date) VALUES (?, ?, ?, ?)";
            PreparedStatement borrowStmt = conn.prepareStatement(insertBorrowSql);
            borrowStmt.setString(1, bookTitle);
            borrowStmt.setString(2, memberId);
            borrowStmt.setDate(3, java.sql.Date.valueOf(borrowDate));
            borrowStmt.setDate(4, java.sql.Date.valueOf(returnDate));
            borrowStmt.executeUpdate();

            confirmed = true;
            showSuccessAlert("Success", "Book borrowed successfully!");
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to borrow book: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
