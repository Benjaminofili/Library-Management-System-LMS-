/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import data.Myconnection;
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

        String insertSql = "INSERT INTO users (uname, fname, lname, email, pass, profileimage, registration_code, role, approved) VALUES (?, ?, ?, ?, ?, UNHEX('89504e470d0a1a0a0000000d49484452000000300000003008060000005702f987000000017352474200aece1ce9000003f5494441546843ed995be84f5914c73fee9706b9246a78e1c535770f42536a240a0f4a4332d3f0e45628228548944b1ec64cc610353591cc083549787197eb0ba91965929931645c32385f9d9f56c739bfbdf739e7fffbcdbffeebf1f75b6bedef77afbdd65e7b9d66347269d6c8f1d344a0de112c3302ed80d9c03c607806b1ebc077c00fc0d332c897416033b03427986f81f9396ddf9b1521b00d58546471637b009895c7571e02bd80df52167b05ec05b602b733c0f40396007380d6293a03819b214442096c07162616f805980afc17b230d002380c4c4ed8fd08ccf4f51542e01a30c838fe13e80ebcf15d2c43af39f010e86afe5704fbfbf8f5257019186a1cae8f767cb5cf02013aeb805546ff0a30cc65ef43e07b60ae71341a38ef729cf3ff51c03963bb27cab72fabf972111800dc300e1485ab0e703d800bc0a709bdfbc048e00f87fd1040bb5f91aa89ed22f0d6385a096c742cfe8d475d5f03ac75f8591155a90d462713673502fb812f62274ad86e8e45a52b1b1f5119dde7507c64123bf39ea846c0eebe4a9eabda587d1f12aee8ab3ad9d29caa9fe56457dcd30888eafc140722550f559110f1a9643f9b7b22b5edc8226077b3a5c725f517d039047dd486fc0d7471d828f2afabe5421a810ec093d848ed411b0f60a1c7a7e2d2758ca4f7d2b41d1d935d6c9a0335686ad424ba03bef22020a2ad3cf4ac8a724a3bec92dde62e581015939dd6208dc02d404d976430a01ede25473cf224e9e30430d1e5386e5fd4c6488445983e481a017b1c7c422c676d81e71e60ac8a8ea622e7239998ca222010c966af1a30af3ec738a80901adf70fa044ab262a109d7cb6bd1e04b4a6eaf5d71900f51ed69b39546a16010bac273021fee1d7a8477a108aba5e11288033d334280217cd58641c70a6211005f81c0b9c8ef52f45adcd086b9b56857446d50b497c6b75009e60d5e3c0e7b1954630cab10f92464003aa7f1d3ac1280a18d8e3d33e79dff834736ad21e170050c454809f55dbcc2c0276da76377aa8f4f544f159f45e9e1487bc0f20009217d12ce81e702c7a561e054e7afa3b0b8c8975b744649625ed7c1f346a7bd5fea6c9f4a85f39e80928a93603f829c3f69344e719f4a091cf43c0b4d879daeda99dd46e9721a70045cf8a8e6de5c6d6406d71da42ae66cd2690da58b5b39ba2245f5e06ea141f959b3a3923caf5a8977f0d96547beb29bda337c9ef59005c11905d9e5ebf2cc23b5c13701f0202a31d480eaaca0299e5e7a3c74b9e1cb036770095c65a88f77bc1370215d0aa16e31b9881731e6ad70f25205b5d2cba601a421afc0387055d6672d7f4135372e7ed142f342a75fdc89704ab819866f96a792b6399a48e2a8b40eb5bdaffe6336be8ae97aa9f27894b0550d4591381a23b58d4fe1dec65a331bc8f6ce50000000049454e44ae426082'), ?, ?, ?";

        try (Connection conn = dbConnection.getdbconnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setString(1, username);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, email);
            stmt.setString(5, password); // Hash before storing
            stmt.setString(6, registrationCode);
            stmt.setString(7, "Librarian");
            stmt.setBoolean(8, false);

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