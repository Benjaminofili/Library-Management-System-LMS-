package library.management.system.lms;

import data.Myconnection;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML
    private TextField Ufield;

    @FXML
    private PasswordField Pfield;

    @FXML
    private int currentUserId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // No special initialization needed here
    }

    public void handleLogin(ActionEvent event) {
        String username = Ufield.getText();
        String password = Pfield.getText();

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            showErrorAlert("Please fill in both the username and password fields.");
            return;
        }

        // Instead of returning a boolean, this method now returns the user id
        int userId = authenticateUser(username, password);

        if (userId != -1) {  // Authentication succeeded

            try {
                showInformationAlert("Login successful!");

                // Load the Dashboard FXML and get its controller
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                Parent fxmlDocumentRoot = loader.load();

                // Assume DashboardController has a method to receive the user id.
                DashboardController dashboardController = loader.getController();
                dashboardController.setUserId(userId);

                // Set the scene and show the Dashboard
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(fxmlDocumentRoot);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                System.err.println("Failed to load Dashboard: " + e.getMessage());
                showErrorAlert("An unexpected error occurred while loading the dashboard. Please try again later.");
            }
        } else {
            // Authentication failed
            showErrorAlert("Invalid username or password.");
        }
    }

    /**
     * Authenticates the user against the database. Returns the user's id if
     * authentication is successful, or -1 if the credentials are invalid.
     */
    public int authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE uname = ? AND pass = ?";
        try {
            Myconnection db = Myconnection.getInstance();
            Connection con = db.getdbconnection();
            if (con == null) {
                System.out.println("Database connection failed!");
                return -1;
            }
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username.trim());
            statement.setString(2, password.trim());
            ResultSet resultSet = statement.executeQuery();

            // If a record exists, return its user id
            if (resultSet.next()) {
                int userId = resultSet.getInt("id"); // Assumes your table has an "id" column
                System.out.println("User authenticated successfully! UserID: " + userId);
                return userId;
            } else {
                System.out.println("Invalid username or password.");
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error: " + e.getMessage());
            return -1;
        }
    }

    @FXML
    private void handleSignupRedirect(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Signup.fxml"));
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

    private void showErrorAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInformationAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
