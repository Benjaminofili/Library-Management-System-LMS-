/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import Class.BookDAO;
import Class.UserIdAware;
import Class.UserImageLoader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author benja
 */
public class ReportsController implements Initializable, UserIdAware {

    private int userId;

    /**
     *
     * @param userId
     */
    @Override
    public void setUserId(int userId) {
        this.userId = userId;
        loadUserImage();
    }

    /**
     * Initializes the controller class.
     *
     */
    @FXML
    private ImageView profilePic;
    @FXML
    private LineChart<String, Number> borrowTrendChart;
    @FXML
    private Label totalBorrowedLabel;
    @FXML
    private VBox mostBorrowedBooksBox;
    @FXML
    private VBox mostActiveMembersBox;
    @FXML
    private BarChart<String, Number> borrowedBooksChart;
@FXML private CategoryAxis xAxis;
@FXML private NumberAxis yAxis;

    private final BookDAO bookDAO = new BookDAO();

    private void loadUserImage() {
        Image userImage = UserImageLoader.loadUserImage(userId);
        profilePic.setImage(userImage);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
//        loadChartData();
        displayBorrowingStatistics();
        displayBorrowingChart();
    }

    private void loadChartData() {
        // Create a new series for "Borrow Trend"
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Borrow Trend");

        // Populate the series with data. Replace these with real values as needed.
        series.getData().add(new XYChart.Data<>("Jan", 20));
        series.getData().add(new XYChart.Data<>("Feb", 35));
        series.getData().add(new XYChart.Data<>("Mar", 40));
        series.getData().add(new XYChart.Data<>("Apr", 30));
        series.getData().add(new XYChart.Data<>("May", 50));

        // Clear any previous data and add the new series
        borrowTrendChart.getData().clear();
        borrowTrendChart.getData().add(series);
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

    private void displayBorrowingStatistics() {
        int totalBorrowed = bookDAO.getTotalBooksBorrowed();
        List<String> mostBorrowedBooks = bookDAO.getMostBorrowedBooks();
        List<String> mostActiveMembers = bookDAO.getMostActiveMembers();

        totalBorrowedLabel.setText("📚 Total Books Borrowed: " + totalBorrowed);

        mostBorrowedBooksBox.getChildren().clear();
        mostBorrowedBooks.forEach(book -> mostBorrowedBooksBox.getChildren().add(new Label(book)));

        mostActiveMembersBox.getChildren().clear();
        mostActiveMembers.forEach(member -> mostActiveMembersBox.getChildren().add(new Label(member)));
    }

 private void displayBorrowingChart() {
    // Set up X and Y axes
    xAxis.setLabel("Book Title");
    yAxis.setLabel("Times Borrowed");

    borrowedBooksChart.setTitle("Most Borrowed Books");
    borrowedBooksChart.getData().clear(); // Clear previous data

    // Get data from BookDAO
    BookDAO bookDAO = new BookDAO();
    List<String> mostBorrowedBooks = bookDAO.getMostBorrowedBooks();

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Borrowing Frequency");

    mostBorrowedBooks.forEach((book) -> {
        // Extract title and borrow count from formatted string
        String title = book.split(" \\(")[0]; // Gets the book title
        int count = Integer.parseInt(book.replaceAll("\\D+", "")); // Gets the borrow count

        series.getData().add(new XYChart.Data<>(title, count));
        });

    borrowedBooksChart.getData().add(series);
}

}
