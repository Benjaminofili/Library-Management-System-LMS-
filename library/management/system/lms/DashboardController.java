package library.management.system.lms;

import model.Book;
import service.BookManager;
import service.ISBNValidator;
import data.Myconnection;
import util.UserIdAware;
import util.UserImageLoader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DashboardController implements Initializable, UserIdAware {

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
    private Label adminNameLabel1;
    @FXML
    private TextField bookNameField, authorField, isbnField, priceField, categoryField;
    @FXML
    private ImageView profilePic;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterDropdown;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> bookNameColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, Double> priceColumn;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, String> categoryColumn;
    @FXML
    private TableColumn<Book, String> availabilityColumn;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private final BookManager bookManager = new BookManager();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        // Convert the boolean availability to a string
        availabilityColumn.setCellValueFactory(cellData -> {
            boolean isAvailable = cellData.getValue().isAvailability(); // Assuming you have a getter for availability
            return new SimpleStringProperty(isAvailable ? "Available" : "Not Available");
        });

        // Load books from the database
        loadBooks();

    }

    private void loadUserImage() {
        Image userImage = UserImageLoader.loadUserImage(userId);
        profilePic.setImage(userImage);
    }

    private void updateAdminName() {
        String adminName = bookManager.getAdminName(userId);
        adminNameLabel.setText("Welcome Back, "
                + "Librarian " + adminName);
        adminNameLabel1.setText("Welcome Back, Librarian " + adminName);
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

    private void loadBooks() {
        try {
            // Obtain database connection
// Get the singleton instance once
            Myconnection db = Myconnection.getInstance();

// Reuse the same connection
            Connection con = db.getdbconnection(); // ✅

            String query = "SELECT * FROM books";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getString("name"),
                        resultSet.getString("author"),
                        resultSet.getDouble("price"),
                        resultSet.getString("isbn"),
                        resultSet.getString("category"),
                        resultSet.getBoolean("availability")
                );
                bookList.add(book);
            }

            bookTable.setItems(bookList);
            loadCategories();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        try {
            // Obtain database connection
            // Get the singleton instance once
            Myconnection db = Myconnection.getInstance();

// Reuse the same connection
            Connection con = db.getdbconnection(); // ✅
            PreparedStatement statement = con.prepareStatement("SELECT DISTINCT category FROM books");
            ResultSet resultSet = statement.executeQuery();

            filterDropdown.getItems().clear(); // Clear previous items
            filterDropdown.getItems().add("All Categories"); // Default option
            while (resultSet.next()) {
                filterDropdown.getItems().add(resultSet.getString("category"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        String filterCategory = filterDropdown.getValue();

        if (searchText == null || searchText.isEmpty()) {
            searchText = ""; // Default to match all
        }
        if (filterCategory == null || filterCategory.isEmpty()) {
            filterCategory = "All Categories"; // Default category filter
        }

        bookList.clear(); // Clear the current list

        try {
            // Get the singleton instance once
            Myconnection db = Myconnection.getInstance();

// Reuse the same connection
            Connection con = db.getdbconnection(); // ✅
            String query = "SELECT * FROM books WHERE (name LIKE ? OR author LIKE ? OR isbn LIKE ?)";
            if (!"All Categories".equals(filterCategory)) {
                query += " AND category = ?";
            }
            query += " AND availability = TRUE"; // Optional: Show only available books

            try (PreparedStatement statement = con.prepareStatement(query)) {
                // Set query parameters
                statement.setString(1, "%" + searchText + "%");
                statement.setString(2, "%" + searchText + "%");
                statement.setString(3, "%" + searchText + "%");
                if (!"All Categories".equals(filterCategory)) {
                    statement.setString(4, filterCategory);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Book book = new Book(
                                resultSet.getString("name"),
                                resultSet.getString("author"),
                                resultSet.getDouble("price"),
                                resultSet.getString("isbn"),
                                resultSet.getString("category"),
                                resultSet.getBoolean("availability")
                        );
                        bookList.add(book);
                    }
                    bookTable.setItems(bookList); // Update table with filtered data
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddBook() {
        // Get data from input fields
        String name = bookNameField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText().trim();
        String category = categoryField.getText();
        double price = Double.parseDouble(priceField.getText());
        boolean availability = true; // Default availability for new books

        if (!ISBNValidator.isValidISBN(isbn)) {
            showErrorAlert("Invalid ISBN", "Please enter valid ISBN-10 or ISBN-13");
            return;
        }

        // Call the addBook method from BookManager
        BookManager manager = new BookManager();
        boolean success = manager.addBook(name, author, price, isbn, category, availability);

        if (success) {
            showInformationAlert("Book added successfully!");
            System.out.println("Book added successfully!");
            bookNameField.clear();
            authorField.clear();
            isbnField.clear();
            priceField.clear();
            categoryField.clear();
            loadBooks();

        } else {
            System.out.println("Failed to add the book. Please check your input.");
        }
    }

    @FXML
    private void redirectToBookManagement(ActionEvent event) {
        try {
            // Load the Book Management Page
            Parent fxmlDocumentRoot = FXMLLoader.load(getClass().getResource("BookManagement.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlDocumentRoot);
            stage.setScene(scene);
            stage.show();
            stage.setTitle("Book Management");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     public void handleLogout(ActionEvent event) throws IOException {
    // Navigate to login page
    Parent loginRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
    Scene scene = new Scene(loginRoot);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
    
}
     
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInformationAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
