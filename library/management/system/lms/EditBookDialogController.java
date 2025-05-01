/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import model.Book;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author benja
 */
public class EditBookDialogController implements Initializable {

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
    private CheckBox availabilityCheckBox;
    @FXML
    private TextField quantityField;

    private Book originalBook;

    public void setBookData(Book book) {
        this.originalBook = book;
        // Populate fields
        titleField.setText(book.getName());
        authorField.setText(book.getAuthor());
        priceField.setText(String.valueOf(book.getPrice()));
        isbnField.setText(book.getIsbn());
        categoryField.setText(book.getCategory());
        availabilityCheckBox.setSelected(book.isAvailability());
        quantityField.setText(String.valueOf(book.getQuantity()));

    }

    public Book getUpdatedBook() {
        // Return new Book object with updated values
        if (quantityField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Quantity is required");
        }

        return new Book(
                originalBook.getId(),
                titleField.getText(),
                authorField.getText(),
                Double.parseDouble(priceField.getText()),
                isbnField.getText(),
                categoryField.getText(),
                availabilityCheckBox.isSelected(),
                Integer.parseInt(quantityField.getText())
        );

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
