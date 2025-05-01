/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import model.Book;
import data.BookDAO;
import service.ISBNValidator;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author benja
 */
public class AddBookDialogController implements Initializable {

    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField isbnField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField quantityField;
    @FXML
    private ComboBox<String> existingCategoriesCombo;
    @FXML
    private CheckBox availabilityCheckBox;
    /**
     * Initializes the controller class.
     */

    private boolean usingNewCategory = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadExistingCategories();

        // Set up listeners
        existingCategoriesCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                categoryField.clear();
                usingNewCategory = false;
            }
        });

        categoryField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                existingCategoriesCombo.getSelectionModel().clearSelection();
                usingNewCategory = true;
            }
        });
    }

    public Book getValidatedBook() throws IllegalArgumentException {

        if (quantityField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Quantity is required");
        }

        // Trim and validate all fields
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String rawISBN = isbnField.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();

        // Validate required fields
        if (title.isEmpty() || author.isEmpty() || rawISBN.isEmpty()) {
            throw new IllegalArgumentException("Title, Author, and ISBN are required fields");
        }

        // Validate ISBN format
        if (!ISBNValidator.isValidISBN(rawISBN)) {
            throw new IllegalArgumentException("Invalid ISBN format");
        }

        // Validate price
        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                throw new IllegalArgumentException("Price must be greater than 0");
            }

            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity < 0) {
                    throw new IllegalArgumentException("Quantity cannot be negative");
                }
                return new Book(
                        0, // Temporary ID (will be replaced by database)
                        title,
                        author,
                        price,
                        ISBNValidator.cleanISBN(rawISBN),
                        category,
                        availabilityCheckBox.isSelected(),
                        quantity
                );

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid quantity format");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format");
        }
    }

    private void loadExistingCategories() {
        try {
            List<String> categories = new BookDAO().getAllCategories();
            existingCategoriesCombo.getItems().setAll(categories);
        } catch (SQLException e) {
            System.err.println("Error loading categories: " + e.getMessage());
        }
    }

    public String getCategory() throws IllegalArgumentException {
        if (usingNewCategory) {
            String newCat = categoryField.getText().trim();
            if (newCat.isEmpty()) {
                throw new IllegalArgumentException("Category cannot be empty");
            }
            return newCat;
        } else if (existingCategoriesCombo.getSelectionModel().getSelectedItem() != null) {
            return existingCategoriesCombo.getSelectionModel().getSelectedItem();
        }
        throw new IllegalArgumentException("Please select or enter a category");
    }
}
