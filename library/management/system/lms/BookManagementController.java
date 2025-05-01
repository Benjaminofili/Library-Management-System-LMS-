/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import model.Book;
import data.BookDAO;
import service.BookManager;
import util.UserIdAware;
import util.UserImageLoader;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ButtonType;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * FXML Controller class for Book Management
 */
public class BookManagementController implements Initializable, UserIdAware {

    private int userId;

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
        loadUserImage();
        updateAdminName();
    }

    @FXML
    private Label adminNameLabel;
    // FXML Fields 
    //fields for tableview
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> isbnCol;
    @FXML
    private TableColumn<Book, Double> priceCol;
    @FXML
    private TableColumn<Book, String> categoryCol;
    @FXML
    private TableColumn<Book, Integer> quantityCol;
    @FXML
    private TableColumn<Book, Boolean> statusCol;
    @FXML
    private TableColumn<Book, Void> actionsCol;
    @FXML
    private ImageView profilePic;

    //fields and class for functions
    private BookDAO bookDAO;
    private ObservableList<Book> bookData = FXCollections.observableArrayList();
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterCategory;
    @FXML
    private ComboBox<String> filterStatus;

//  fields for filtering
    private ObservableList<Book> masterData = FXCollections.observableArrayList();
    private FilteredList<Book> filteredData = new FilteredList<>(masterData);
    private final ObjectProperty<Predicate<Book>> searchPredicate = new SimpleObjectProperty<>();
    private final ObjectProperty<Predicate<Book>> categoryPredicate = new SimpleObjectProperty<>();
    private final ObjectProperty<Predicate<Book>> statusPredicate = new SimpleObjectProperty<>();

 private final BookManager bookManager = new BookManager();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateAdminName();
        bookDAO = new BookDAO();
        setupTableColumns();
        loadBooksFromDatabase();
        setupActionColumn();
        setupActionColumn();
        bookTable.setItems(filteredData);

        // Set up filter predicates
        setupFilterPredicates();

        // Initialize filter dropdowns
        initializeCategoryFilter();
        initializeStatusFilter();

        // Set up search/filter listeners
        setupSearchListener();

    }

    private void loadUserImage() {
        Image userImage = UserImageLoader.loadUserImage(userId);
        profilePic.setImage(userImage);
    }

    private void updateAdminName() {
    String adminName = bookManager.getAdminName(userId);
    adminNameLabel.setText("Welcome Back, Librarian " + adminName);
}
    /**
     * Handle navigation between different sections.
     */
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
            case "borrowBookButton":
                fxmlFile = "BorrowBook.fxml";
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

   private void setupTableColumns() {
    // Set up property bindings for regular columns
    titleCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
    isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
    priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
    quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    statusCol.setCellValueFactory(new PropertyValueFactory<>("availability"));

    // Custom cell factory for title column (text wrapping)
    titleCol.setCellFactory(tc -> new TableCell<Book, String>() {
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

    // Custom cell factory for author column (text wrapping)
    authorCol.setCellFactory(tc -> new TableCell<Book, String>() {
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

    // Custom cell factory for category column (text wrapping)
    categoryCol.setCellFactory(tc -> new TableCell<Book, String>() {
        private final Text text = new Text();

        {
            setGraphic(text);
            text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10)); // Wrapping for categories
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            text.setText(empty || item == null ? "" : item);
        }
    });

    // Custom cell factory for price column (formatted currency)
    priceCol.setCellFactory(column -> new TableCell<Book, Double>() {
        @Override
        protected void updateItem(Double price, boolean empty) {
            super.updateItem(price, empty);
            setText(empty || price == null ? null : String.format("$%.2f", price));
        }
    });

    // Custom cell factory for quantity column (color-coded stock levels)
    quantityCol.setCellFactory(col -> new TableCell<Book, Integer>() {
        @Override
        protected void updateItem(Integer quantity, boolean empty) {
            super.updateItem(quantity, empty);
            if (empty || quantity == null) {
                setText("");
                setStyle("");
            } else {
                setText(quantity.toString());
                setStyle(quantity <= 0 ? "-fx-text-fill: red;" : "");
            }
        }
    });

    // Custom cell factory for status column (availability indicator)
    statusCol.setCellFactory(column -> new TableCell<Book, Boolean>() {
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item ? "Available" : "Checked Out");
                setStyle(item ? "-fx-text-fill: #27AE60;" : "-fx-text-fill: #E74C3C;");
            }
        }
    });

    bookTable.setSortPolicy(param -> true);
    bookTable.setItems(bookData);
}


    /**
     * Loads all books into the TableView.
     */
    private void setupActionColumn() {
        actionsCol.setCellFactory(param -> new TableCell<Book, Void>() {
            // Create HBoxes for two rows
            private final HBox topButtons = new HBox(10);
            private final HBox bottomButtons = new HBox(10);
            // Create a VBox to hold the two rows
            private final VBox buttonsBox = new VBox(5);

            // Define the buttons for each row
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button borrowBtn = new Button("Borrow");
            private final Button returnBtn = new Button("Return");

            {
                // Style the buttons
                editBtn.setStyle("-fx-background-color: #F1C40F; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
                borrowBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
                returnBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white;");

                // Configure HBox for the top row with Edit and Delete buttons
                topButtons.setAlignment(Pos.CENTER);
                topButtons.getChildren().addAll(editBtn, deleteBtn);

                // Configure HBox for the bottom row with Borrow and Return buttons
                bottomButtons.setAlignment(Pos.CENTER);
                bottomButtons.getChildren().addAll(borrowBtn, returnBtn);

                // Add both rows into the VBox
                buttonsBox.getChildren().addAll(topButtons, bottomButtons);
                buttonsBox.setAlignment(Pos.CENTER);

                // Set up action handlers
                editBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    handleEditBook(book);
                });

                deleteBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    handleDeleteBook(book);
                });

                borrowBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    handleBorrowBook(book);
                });

                returnBtn.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    handleReturnBook(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });
    }

    private void loadBooksFromDatabase() {
        try {
            masterData.setAll(bookDAO.getAllBooks());
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Could not load books");
        }
    }

    @FXML
    private void handleAddBook(ActionEvent event) {
        try {
            Dialog<Book> dialog = new Dialog<>();
            dialog.setTitle("Add New Book");

            // Load FXML content
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddBookDialog.fxml"));
            dialog.getDialogPane().setContent(loader.load());

            // Custom button texts
            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, cancelButtonType);

            // Get reference to the Add button
            Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);

            // Handle Add button click with validation
            addButton.addEventFilter(ActionEvent.ACTION, ae -> {
                AddBookDialogController controller = loader.getController();
                try {
                    Book book = controller.getValidatedBook();

                    // Additional validation checks
                    if (book.getPrice() <= 0) {
                        throw new IllegalArgumentException("Price must be greater than 0");
                    }

                } catch (IllegalArgumentException e) {
                    // Show error and prevent dialog close
                    showErrorAlert("Validation Error", e.getMessage());
                    ae.consume(); // Prevent dialog closing
                }
            });

            // Result converter (only called if validation passed)
            dialog.setResultConverter(buttonType -> {
                if (buttonType == addButtonType) {
                    AddBookDialogController controller = loader.getController();
                    return controller.getValidatedBook();
                }
                return null;
            });

            // Process valid result
            Optional<Book> result = dialog.showAndWait();
            result.ifPresent(book -> {
                try {
                    int newId = bookDAO.addBook(book);
                    book.setId(newId);
                    bookData.add(book);
                    showSuccessAlert("Success", "Book added successfully!\nNew ID: " + newId);
                    loadBooksFromDatabase();
                } catch (SQLException e) {
                    showErrorAlert("Database Error",
                            "Failed to save book:\n" + e.getMessage()
                            + "\nSQL State: " + e.getSQLState());
                }
            });

        } catch (IOException e) {
            showErrorAlert("System Error", "Failed to load dialog: " + e.getMessage());
        }
    }

    private void handleEditBook(Book book) {
        try {
            // Load the edit dialog
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("EditBookDialog.fxml"));
            DialogPane dialogPane = loader.load();
            URL fxmlPath = getClass().getResource("EditBookDialog.fxml");
            System.out.println("FXML Path: " + fxmlPath);
            // Get controller reference
            EditBookDialogController controller = loader.getController();
            controller.setBookData(book);

            // Create dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Edit Book");

            // Add OK/Cancel buttons (implicitly added)
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Show dialog and wait for response
            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Book updatedBook = controller.getUpdatedBook();
                bookDAO.updateBook(updatedBook);

                // Refresh the book list from the database
                loadBooksFromDatabase();

                showSuccessAlert("Book Updated", "Book details updated successfully!");
            }
        } catch (IOException ee) {
            showErrorAlert("Edit Error", "Failed to load dialog: " + ee.getMessage());
            ee.printStackTrace(); // Add this for debugging
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to update book in database");
            e.printStackTrace();
        }
    }

    private void handleDeleteBook(Book book) {
        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Book");
        confirmation.setContentText("Are you sure you want to delete '" + book.getName() + "'?");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bookDAO.deleteBook(book.getId());
                bookData.remove(book);
                showSuccessAlert("Deleted", "Book deleted successfully!");
            } catch (SQLException e) {
                showErrorAlert("Delete Error", "Failed to delete book: " + e.getMessage());
            }
        }
    }

    private void handleBorrowBook(Book book) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Borrow Book");

        // Create and configure the grid pane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add labels and input fields
        TextField bookTitleField = new TextField(book.getName());
        bookTitleField.setEditable(false); // Book title is read-only

        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Enter Member ID");

        DatePicker returnDatePicker = new DatePicker(); // Only selecting return date

        grid.add(new Label("Book Title:"), 0, 0);
        grid.add(bookTitleField, 1, 0);
        grid.add(new Label("Member ID:"), 0, 1);
        grid.add(memberIdField, 1, 1);
        grid.add(new Label("Return Date:"), 0, 2);
        grid.add(returnDatePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Add OK and Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Capture user input
            String memberId = memberIdField.getText();
            LocalDate returnDate = returnDatePicker.getValue();
            LocalDateTime borrowDateTime = LocalDateTime.now(); // Timestamp for borrowing

            if (memberId.isEmpty() || returnDate == null) {
                showErrorAlert("Invalid Input", "All fields must be filled before proceeding!");
                return;
            }

            // Call BookDAO to save the borrowing record
            BookDAO bookDAO = new BookDAO();
            boolean success = bookDAO.borrowBook(book.getId(), memberId, borrowDateTime, returnDate);

            if (success) {
                showSuccessAlert("Success", "Book borrowed successfully!");
                loadBooksFromDatabase();
            } else {
                showErrorAlert("Database Error", "Failed to borrow book. Please try again.");
            }
        }
    }

    private void handleReturnBook(Book book) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Return Book");

        // Configure layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add input fields
        TextField bookTitleField = new TextField(book.getName());
        bookTitleField.setEditable(false); // Book title is read-only

        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Enter Member ID");

        DatePicker returnDatePicker = new DatePicker(); // Select actual return date

        grid.add(new Label("Book Title:"), 0, 0);
        grid.add(bookTitleField, 1, 0);
        grid.add(new Label("Member ID:"), 0, 1);
        grid.add(memberIdField, 1, 1);
        grid.add(new Label("Return Date:"), 0, 2);
        grid.add(returnDatePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Add OK and Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Capture user input
            String memberId = memberIdField.getText();
            LocalDate returnDate = returnDatePicker.getValue(); // User-selected return date

            // Validate input
            if (memberId.isEmpty() || returnDate == null) {
                showErrorAlert("Invalid Input", "All fields must be filled before proceeding!");
                return;
            }

            // Call BookDAO to process the return
            BookDAO bookDAO = new BookDAO();
            boolean success = bookDAO.returnBook(book.getId(), memberId, returnDate);

            if (success) {
                showSuccessAlert("Success", "Book returned successfully!");
                loadBooksFromDatabase();
            } else {
                showErrorAlert("Error", "Failed to return book. Please check details.");
            }
        }
    }

    private void setupFilterPredicates() {
        // Initialize with non-null default predicates
        searchPredicate.set(book -> true);
        categoryPredicate.set(book -> true);
        statusPredicate.set(book -> true);

        filteredData.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            Predicate<Book> combined = searchPredicate.get()
                    .and(categoryPredicate.get())
                    .and(statusPredicate.get());
            return combined;
        }, searchPredicate, categoryPredicate, statusPredicate));
    }

    private void initializeCategoryFilter() {
        try {
            List<String> categories = bookDAO.getAllBooks().stream()
                    .map(Book::getCategory)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            filterCategory.getItems().add("All Categories");
            filterCategory.getItems().addAll(categories);
            filterCategory.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to load categories");
        }
    }

    private void initializeStatusFilter() {
        filterStatus.getItems().addAll(
                "All Statuses",
                "Available",
                "Checked Out"
        );
        filterStatus.getSelectionModel().selectFirst();
    }

    private void setupSearchListener() {
        // Search field listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchPredicate.set(book
                    -> book.getName().toLowerCase().contains(newVal.toLowerCase())
                    || book.getAuthor().toLowerCase().contains(newVal.toLowerCase())
                    || book.getIsbn().contains(newVal)
            );
        });

        // Category filter listener
        filterCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            categoryPredicate.set(book
                    -> newVal.equals("All Categories")
                    || (book.getCategory() != null && book.getCategory().equals(newVal))
            );
        });

        // Status filter listener
        filterStatus.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            statusPredicate.set(book -> {
                if (newVal.equals("All Statuses")) {
                    return true;
                }
                boolean available = newVal.equals("Available");
                return book.isAvailability() == available;
            });
        });
    }

     public void handleLogout(ActionEvent event) throws IOException {
    // Navigate to login page
    Parent loginRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
    Scene scene = new Scene(loginRoot);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
    
}
     
    @FXML
    private void handleClearFilters(ActionEvent event) {
        searchField.clear();
        filterCategory.getSelectionModel().selectFirst();
        filterStatus.getSelectionModel().selectFirst();
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
