/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import Class.BookManager;
import Class.Member;
import Class.MemberDAO;
import Class.MemberDialogHelper;
import Class.User;
import Class.UserIdAware;
import Class.UserImageLoader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author benja
 */
public class MemberManagementController implements Initializable, UserIdAware {

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
    private ImageView profilePic;
    @FXML
    private TableView<Member> memberTable; // Your TableView for member data
    @FXML
    private TableColumn<Member, Integer> memberIdCol;

    @FXML
    private TableColumn<Member, String> firstNameCol;

    @FXML
    private TableColumn<Member, String> lastNameCol;

    @FXML
    private TableColumn<Member, String> emailCol;

    @FXML
    private TableColumn<Member, String> phoneCol;

    @FXML
    private TableView<User> librarianTable;
    @FXML
    private TableColumn<User, Integer> librarianIdCol;
    @FXML
    private TableColumn<User, String> librarianFirstNameCol;
    @FXML
    private TableColumn<User, String> librarianLastNameCol;
    @FXML
    private TableColumn<User, String> librarianEmailCol;
    @FXML
    private TableColumn<User, Button> actionCol1;

    // Optionally, if you have a column for custom action buttons
    @FXML
    private TableColumn<Member, Void> actionsCol;
    private final BookManager bookManager = new BookManager();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        updateAdminName();
        memberIdCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        setupActionsColumn();
        loadMemberData();

        librarianIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        librarianFirstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        librarianLastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        librarianEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        applyTextWrapping(firstNameCol);
        applyTextWrapping(lastNameCol);
        applyTextWrapping(emailCol);
        applyTextWrapping(librarianFirstNameCol);
        applyTextWrapping(librarianLastNameCol);
        applyTextWrapping(librarianEmailCol);

        setupLibrarianActionsColumn(); // Set up approve/reject buttons
        loadPendingLibrarians();
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

    // This method is bound to the "Add Member" button via onAction="#openAddMemberDialog" in FXML
    // Opens the Add Member dialog and processes its result.
    @FXML
    public void openAddMemberDialog() {
        Optional<Member> result = MemberDialogHelper.showAddMemberDialog();
        result.ifPresent(member -> {
            // Validate required fields.
            if (member.getFirstName().isEmpty() || member.getEmail().isEmpty()) {
                showAlert(AlertType.ERROR, "Validation Error", "First Name and Email are required.");
                return;
            }
            try {
                MemberDAO.addMember(member);
                loadMemberData();
                showAlert(AlertType.INFORMATION, "Success", "Member added successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(AlertType.ERROR, "Database Error", "Could not add member: " + ex.getMessage());
            }
        });
    }

    private void setupActionsColumn() {
        actionsCol.setCellFactory(column -> new TableCell<Member, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #F1C40F; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    handleEditMember(member);
                });

                deleteBtn.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    handleDeleteMember(member);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void handleEditMember(Member member) {
        Optional<Member> result = MemberDialogHelper.showEditMemberDialog(member);
        result.ifPresent(updatedMember -> {
            if (updatedMember.getFirstName().isEmpty() || updatedMember.getEmail().isEmpty()) {
                showAlert(AlertType.ERROR, "Validation Error", "First Name and Email are required.");
                return;
            }
            try {
                MemberDAO.updateMember(updatedMember);
                loadMemberData();
                showAlert(AlertType.INFORMATION, "Success", "Member updated successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(AlertType.ERROR, "Database Error", "Could not update member: " + ex.getMessage());
            }
        });
    }

    // Confirms and deletes the given member.
    private void handleDeleteMember(Member member) {
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        Optional<ButtonType> result
                = showConfirmation("Delete Member",
                        "Are you sure you want to delete " + member.getFirstName() + " "
                        + member.getLastName() + "?");
        if (result.isPresent() && result.get() == okButton) {
            try {
                MemberDAO.deleteMember(member.getMemberId());
                loadMemberData();
                showAlert(AlertType.INFORMATION, "Success", "Member deleted successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(AlertType.ERROR, "Database Error", "Could not delete member: " + ex.getMessage());
            }
        }
    }

    // Loads member data using the MemberDAO and sets it on the TableView.
    private void loadMemberData() {
        ObservableList<Member> memberList = MemberDAO.getAllMembers();
        memberTable.setItems(memberList);
    }

    // Load pending librarians who need approval
    private void loadPendingLibrarians() {
        ObservableList<User> pendingLibrarians = MemberDAO.getPendingLibrarians();
        librarianTable.setItems(pendingLibrarians);
    }

    private void setupLibrarianActionsColumn() {
        actionCol1.setCellFactory(column -> new TableCell<User, Button>() { // ✅ Changed Void -> Button
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox buttonPane = new HBox(10, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white;");
                rejectBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");

                // Approve librarian
                approveBtn.setOnAction(event -> {
                    User librarian = getTableView().getItems().get(getIndex());
                    boolean success = MemberDAO.approveLibrarian(librarian.getId());
                    if (success) {
                        librarianTable.getItems().remove(librarian);
                        librarianTable.refresh();
                    }
                });

                // Reject librarian request
                rejectBtn.setOnAction(event -> {
                    User librarian = getTableView().getItems().get(getIndex());
                    boolean success = MemberDAO.rejectLibrarian(librarian.getId()); // ✅ New reject method
                    if (success) {
                        librarianTable.getItems().remove(librarian);
                        librarianTable.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonPane); // ✅ Displays both buttons in the column
            }
        });
    }

    private <T> void applyTextWrapping(TableColumn<T, String> column) {
        column.setCellFactory(tc -> new TableCell<T, String>() {
            private final Text text = new Text();

            {
                setGraphic(text);
                text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10)); // Enable text wrapping
                text.setTextAlignment(TextAlignment.LEFT);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                text.setText(empty || item == null ? "" : item);
            }
        });
    }

    // Utility method to display alerts.
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
