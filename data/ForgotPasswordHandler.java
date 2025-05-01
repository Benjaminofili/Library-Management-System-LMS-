/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author benja
 */
public class ForgotPasswordHandler {
  
    private final Myconnection dbConnection;

    public ForgotPasswordHandler() {
        this.dbConnection = Myconnection.getInstance();
    }

    public void handleForgotPassword() {
        // Step 1: Show a dialog to enter username
        Dialog<String> usernameDialog = new Dialog<>();
        usernameDialog.setTitle("Forgot Password");
        usernameDialog.setHeaderText("Enter your username to reset your password");

        // Apply the ancient library scroll theme stylesheet
        try {
            String cssPath = "/css/Login.css";
            java.net.URL cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                usernameDialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
            } else {
                // Fallback inline styles
                usernameDialog.getDialogPane().setStyle(
                    "-fx-background-image: url('/img/green_texture.png'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center; " +
                    "-fx-background-color: rgba(255, 245, 220, 0.9); " +
                    "-fx-border-color: #8B4513; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 10px;"
                );
            }
        } catch (Exception e) {
            // Silently apply fallback styles if there's an error
            usernameDialog.getDialogPane().setStyle(
                "-fx-background-image: url('/img/green_texture.png'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center; " +
                "-fx-background-color: rgba(255, 245, 220, 0.9); " +
                "-fx-border-color: #8B4513; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 10px;"
            );
        }

        ButtonType submitButtonType = new ButtonType("Submit", ButtonType.OK.getButtonData());
        usernameDialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Username");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameInput, 1, 0);

        usernameDialog.getDialogPane().setContent(grid);

        usernameDialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return usernameInput.getText().trim();
            }
            return null;
        });

        usernameDialog.showAndWait().ifPresent(username -> {
            // Step 2: Verify the username exists
            if (username.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a username.");
                return;
            }

            String query = "SELECT * FROM users WHERE uname = ?";
            try (Connection conn = dbConnection.getdbconnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Username exists, now prompt for the code
                    verifyCodeAndProceed(username);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Username not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database.");
            }
        });
    }

    private void verifyCodeAndProceed(String username) {
        // Step 3: Show a dialog to enter the code
        Dialog<String> codeDialog = new Dialog<>();
        codeDialog.setTitle("Verify Identity");
        codeDialog.setHeaderText("Enter the code to verify your identity");

        // Apply the ancient library scroll theme stylesheet
        try {
            String cssPath = "/css/Login.css";
            java.net.URL cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                codeDialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
            } else {
                codeDialog.getDialogPane().setStyle(
                    "-fx-background-image: url('/img/green_texture.png'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center; " +
                    "-fx-background-color: rgba(255, 245, 220, 0.9); " +
                    "-fx-border-color: #8B4513; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 10px;"
                );
            }
        } catch (Exception e) {
            codeDialog.getDialogPane().setStyle(
                "-fx-background-image: url('/img/green_texture.png'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center; " +
                "-fx-background-color: rgba(255, 245, 220, 0.9); " +
                "-fx-border-color: #8B4513; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 10px;"
            );
        }

        ButtonType verifyButtonType = new ButtonType("Verify", ButtonType.OK.getButtonData());
        codeDialog.getDialogPane().getButtonTypes().addAll(verifyButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        TextField codeInput = new TextField();
        codeInput.setPromptText("Enter the code");

        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeInput, 1, 0);
        grid.add(new Label("Hint: The Wise King's Code"), 1, 1);

        codeDialog.getDialogPane().setContent(grid);

        codeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == verifyButtonType) {
                return codeInput.getText().trim();
            }
            return null;
        });

        codeDialog.showAndWait().ifPresent(code -> {
            // Step 4: Verify the code
            if (code.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter the code.");
                return;
            }

            if (code.equals("The Wise King's Code")) {
                // Code is correct, proceed to password reset
                showPasswordResetDialog(username);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Incorrect code. Please try again.");
            }
        });
    }

    private void showPasswordResetDialog(String username) {
        // Step 5: Show dialog to enter new password
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Enter your new password");

        // Apply the ancient library scroll theme stylesheet
        try {
            String cssPath = "/css/Login.css";
            java.net.URL cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
            } else {
                dialog.getDialogPane().setStyle(
                    "-fx-background-image: url('/img/green_texture.png'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center; " +
                    "-fx-background-color: rgba(255, 245, 220, 0.9); " +
                    "-fx-border-color: #8B4513; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 10px;"
                );
            }
        } catch (Exception e) {
            dialog.getDialogPane().setStyle(
                "-fx-background-image: url('/img/green_texture.png'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center; " +
                "-fx-background-color: rgba(255, 245, 220, 0.9); " +
                "-fx-border-color: #8B4513; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 10px;"
            );
        }

        ButtonType resetButtonType = new ButtonType("Reset", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        grid.add(new Label("New Password:"), 0, 0);
        grid.add(newPasswordField, 1, 0);
        grid.add(new Label("Confirm Password:"), 0, 1);
        grid.add(confirmPasswordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resetButtonType) {
                String newPassword = newPasswordField.getText().trim();
                String confirmPassword = confirmPasswordField.getText().trim();

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please fill in both password fields.");
                    return null;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
                    return null;
                }

                if (newPassword.length() < 6) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Password must be at least 6 characters long.");
                    return null;
                }

                // Step 6: Update the password in the database
                String updateQuery = "UPDATE users SET pass = ? WHERE uname = ?";
                try (Connection conn = dbConnection.getdbconnection();
                     PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                    pstmt.setString(1, newPassword);
                    pstmt.setString(2, username);
                    int rowsUpdated = pstmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Password reset successfully. Please log in with your new password.");
                        dialog.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to reset password. Please try again.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the password.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}