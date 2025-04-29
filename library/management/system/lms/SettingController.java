/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import Class.BookManager;
import Class.Myconnection;
import Class.UserIdAware;
import Class.UserImageLoader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author benja
 */
//Kabao
public class SettingController implements Initializable, UserIdAware {

    private int userId;

    /**
     *
     * @param userId
     */
    @Override
       public void setUserId(int userId) {
        this.userId = userId;
        loadUserImage();
        updateAdminName();
    }

    @FXML
    private Label adminNameLabel;

    @FXML
    private TextField firstNameField;

    @FXML
    private ImageView profilePicView;
    
    @FXML
    private ImageView profilePic;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField accountUsernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;
    private final BookManager bookManager = new BookManager();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private void loadUserImage() {
        Image userImage = UserImageLoader.loadUserImage(userId);
        profilePic.setImage(userImage);
    }
    
        private void updateAdminName() {
        String adminName = bookManager.getAdminName(userId);
        adminNameLabel.setText("Welcome Back, Librarian " + adminName);
    }

    @FXML
    private void handleNavigation(ActionEvent event) {
        String buttonId = ((Node) event.getSource()).getId(); // Get the button's ID
        String fxmlFile = "";
        switch (buttonId) {
            case "dashboardButton":
                fxmlFile = "Dashboard.fxml";
                break;
            case "readerManagementButton":
                fxmlFile = "MemberManagement.fxml";
                break;
            case "bookManagementButton":
                fxmlFile = "BookManagement.fxml";
                break;

            case "reportsButton":
                fxmlFile = "Reports.fxml";
                break;
            case "settingsButton":
                fxmlFile = "setting.fxml";
                break;
            default:
                System.out.println("Unknown sidebar option clicked: " + buttonId);
                return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent fxmlDocumentRoot = loader.load();

            // Pass the current user id (stored in a variable, e.g., currentUserId)
            Object controller = loader.getController();
            if (controller instanceof UserIdAware) {
                ((UserIdAware) controller).setUserId(userId);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlDocumentRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + fxmlFile);
        }
    }

    @FXML
    private void handleChangeProfilePic(ActionEvent event) {
        // Create and configure the FileChooser.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        FileChooser.ExtensionFilter imageFilter;
        imageFilter = new FileChooser.ExtensionFilter(
                "Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp");
        fileChooser.getExtensionFilters().add(imageFilter);

        // Show the open dialog.
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                // Read the file's content into a byte array.
                byte[] imageBytes = Files.readAllBytes(file.toPath());

                // Prepare an SQL statement to update the profileImage column.
                String sql = "UPDATE users SET profileImage = ? WHERE id = ?";
                Myconnection db = Myconnection.getInstance();
                Connection con = db.getdbconnection();

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setBytes(1, imageBytes);
                    ps.setInt(2, userId);

                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected > 0) {
                        // Update the ImageView to instantly reflect the new profile image.
                        Image image = new Image(new ByteArrayInputStream(imageBytes));
                        profilePicView.setImage(image);
                        System.out.println("Profile image updated successfully.");
                    } else {
                        showErrorAlert("Update Failed", "Could not update the profile image in the database.");
                    }
                }
            } catch (IOException ex) {
                System.err.println("Error reading file: " + ex.getMessage());
                showErrorAlert("File Read Error", "Error reading the selected file.");
            } catch (SQLException ex) {
                System.err.println("SQL Error: " + ex.getMessage());
                showErrorAlert("Database Error", "Database error occurred while updating the profile image.");
            }
        }
    }

    public void handleUpdateProfile(ActionEvent event) {
        String Fname = firstNameField.getText();
        String Lname = lastNameField.getText();
        String Email = emailField.getText();
        String Uname = accountUsernameField.getText();
        String password = passwordField.getText();
        String repeat = repeatPasswordField.getText();

        // Check if any required field is empty
        if (Fname == null || Fname.trim().isEmpty()
                || Lname == null || Lname.trim().isEmpty()
                || Email == null || Email.trim().isEmpty()
                || Uname == null || Uname.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || repeat == null || repeat.trim().isEmpty()) {
            showErrorAlert("Update Failed", "All fields must be filled out.");
            return; // Exit early if any field is empty
        }

        if (!password.equals(repeat)) {
            showErrorAlert("Update Failed", "Passwords do not match.");
            return; // Exit early if passwords are not the same
        }

        String pass = password;
        String sql = "UPDATE users SET uname = ?, fname = ?, lname = ?, email = ?, pass = ? WHERE id = ?";

        try {
            Myconnection db = Myconnection.getInstance();
            Connection con = db.getdbconnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, Uname);
            ps.setString(2, Fname);
            ps.setString(3, Lname);
            ps.setString(4, Email);
            ps.setString(5, pass);
            ps.setInt(6, userId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                // Optionally, update any UI elements or internal user model
                showSuccessAlert("Success", "Profile updated successfully!");
                clearfield();
            } else {
                showErrorAlert("Update Failed", "No record was updated. Please verify the user id.");
            }
        } catch (SQLException ex) {
            // Log and show database errors
            showErrorAlert("Database Error", "Error updating profile: " + ex.getMessage());
        } catch (Exception ex) {
            // Catch any other exceptions
            showErrorAlert("Error", "An unexpected error occurred: " + ex.getMessage());
        }

    }

    private void clearfield() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        accountUsernameField.clear();
        passwordField.clear();
        repeatPasswordField.clear();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
