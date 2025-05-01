/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import model.Member;
/**
 *
 * @author benja
 */
public class MemberDialogHelper {

  public static Optional<Member> showAddMemberDialog() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Add New Member");
        dialog.setHeaderText("Enter new member information");

        // Apply the CSS stylesheet
        dialog.getDialogPane().getStylesheets().add(
            MemberDialogHelper.class.getResource("/css/memberdialog.css").toExternalForm()
        );

        // Define the "Add" and "Cancel" buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the grid layout for the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20)); // Adjusted padding for better balance

        // Create input fields
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.setPrefWidth(300); // Set a reasonable width for text fields

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.setPrefWidth(300);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefWidth(300);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        phoneField.setPrefWidth(300);

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.setPrefWidth(300);

        // Add labels and fields to the grid
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convert the dialog result to a Member object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Member member = new Member();
                member.setFirstName(firstNameField.getText().trim());
                member.setLastName(lastNameField.getText().trim());
                member.setEmail(emailField.getText().trim());
                member.setPhone(phoneField.getText().trim());
                member.setAddress(addressField.getText().trim());
                return member;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static Optional<Member> showEditMemberDialog(Member member) {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Edit member information for: " 
                             + member.getFirstName() + " " + member.getLastName());

        // Apply the CSS stylesheet
        dialog.getDialogPane().getStylesheets().add(
            MemberDialogHelper.class.getResource("/css/memberdialog.css").toExternalForm()
        );

        // Define the "Update" and "Cancel" buttons
        ButtonType updateButtonType = new ButtonType("Update", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the grid layout for the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20)); // Adjusted padding for better balance

        // Create input fields with existing member data
        TextField firstNameField = new TextField(member.getFirstName());
        firstNameField.setPrefWidth(300);

        TextField lastNameField = new TextField(member.getLastName());
        lastNameField.setPrefWidth(300);

        TextField emailField = new TextField(member.getEmail());
        emailField.setPrefWidth(300);

        TextField phoneField = new TextField(member.getPhone());
        phoneField.setPrefWidth(300);

        TextField addressField = new TextField(member.getAddress());
        addressField.setPrefWidth(300);

        // Add labels and fields to the grid
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convert the dialog result to a Member object
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                Member updatedMember = new Member();
                updatedMember.setMemberId(member.getMemberId()); // Keep the same ID
                updatedMember.setFirstName(firstNameField.getText().trim());
                updatedMember.setLastName(lastNameField.getText().trim());
                updatedMember.setEmail(emailField.getText().trim());
                updatedMember.setPhone(phoneField.getText().trim());
                updatedMember.setAddress(addressField.getText().trim());
                return updatedMember;
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
