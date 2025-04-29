/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import Class.Myconnection;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author benja
 */
public class SignupController implements Initializable {

    private final Myconnection dbConnection;

    public SignupController() {
        this.dbConnection = Myconnection.getInstance();
    }

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField registrationCodeField;
    @FXML
    private Label statusLabel;
    private final String REQUIRED_CODE = "The Wise King's Code"; // Library-specific access code

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleSignup() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String registrationCode = registrationCodeField.getText().trim();
         String username = firstName.toLowerCase();

        if (!registrationCode.equals(REQUIRED_CODE)) {
        statusLabel.setText("❌ Invalid registration code. Access denied.");
        return;
    }

    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
        statusLabel.setText("❌ Please fill all fields.");
        return;
    }

    String insertSql = "INSERT INTO users (uname, fname, lname, email, pass, registration_code, role, approved) VALUES (?, ?, ?, ?, ?, ?, 'Librarian', FALSE)";

    try (Connection conn = dbConnection.getdbconnection();
            PreparedStatement stmt = conn.prepareStatement(insertSql)) {

        stmt.setString(1, username); 
        stmt.setString(2, firstName);
        stmt.setString(3, lastName);
        stmt.setString(4, email);
        stmt.setString(5, password); // Hash before storing
        stmt.setString(6, registrationCode);

        stmt.executeUpdate();
        statusLabel.setText("✅ Account request sent. Admin approval required.");
    } catch (SQLException e) {
        e.printStackTrace();
        statusLabel.setText("❌ Error creating account: " + e.getMessage()); // ✅ Show exact error message
    }
}

     @FXML
    private void handleGoToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent signupRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene signupScene = new Scene(signupRoot);
            stage.setScene(signupScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading Signup.fxml");
        }
    }
}
