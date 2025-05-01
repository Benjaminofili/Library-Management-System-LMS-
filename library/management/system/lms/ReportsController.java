/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import data.ReportDao;
import util.UserIdAware;
import util.UserImageLoader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
    private TableView<ReportDao.Transaction> transactionTable;

    @FXML
    private TableColumn<ReportDao.Transaction, String> bookTitleColumn;

    @FXML
    private TableColumn<ReportDao.Transaction, String> borrowerNameColumn;

    @FXML
    private TableColumn<ReportDao.Transaction, String> borrowedDateColumn;

    @FXML
    private TableColumn<ReportDao.Transaction, String> dueDateColumn;

    @FXML
    private TableColumn<ReportDao.Transaction, String> statusColumn;

    @FXML
    private TableColumn<ReportDao.Transaction, Integer> cnrColumn;

    @FXML
    private Label totalBooksLabel;

    @FXML
    private Label booksBorrowedLabel;

    @FXML
    private Label registeredReadersLabel;

    @FXML
    private Label overdueBooksLabel;

    @FXML
    private PieChart categoryPieChart;

    @FXML
    private LineChart<String, Number> borrowingTrendsLineChart;

    private ReportDao reportDao;

    private void loadUserImage() {
        Image userImage = UserImageLoader.loadUserImage(userId);
        profilePic.setImage(userImage);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        reportDao = new ReportDao();

        // Initialize statistics cards
        ReportDao.StatisticsData stats = reportDao.getStatisticsData();
        totalBooksLabel.setText(String.valueOf(stats.getTotalBooks()));
        booksBorrowedLabel.setText(String.valueOf(stats.getBooksBorrowed()));
        registeredReadersLabel.setText(String.valueOf(stats.getRegisteredReaders()));
        overdueBooksLabel.setText(String.valueOf(stats.getOverdueBooks()));

        // Initialize transaction table
     bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        bookTitleColumn.setCellFactory(column -> new TextFieldTableCell<ReportDao.Transaction, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text text = new Text(item);
                    text.setWrappingWidth(bookTitleColumn.getWidth() - 10); // Adjust for padding
                    text.setStyle("-fx-font-family: Georgia; -fx-font-size: 14px; -fx-fill: #4A2C2A;");
                    setGraphic(text);
                    setText(null);
                }
            }
        });

        borrowerNameColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        borrowerNameColumn.setCellFactory(column -> new TextFieldTableCell<ReportDao.Transaction, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text text = new Text(item);
                    text.setWrappingWidth(borrowerNameColumn.getWidth() - 10); // Adjust for padding
                    text.setStyle("-fx-font-family: Georgia; -fx-font-size: 14px; -fx-fill: #4A2C2A;");
                    setGraphic(text);
                    setText(null);
                }
            }
        });

        borrowedDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        cnrColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        transactionTable.setItems(reportDao.getTransactionData());

        // Initialize pie chart
        categoryPieChart.setData(reportDao.getCategoryDistributionData());
        categoryPieChart.setLegendVisible(true);
        categoryPieChart.setTitle("Category Distribution");

        // Initialize line chart
        borrowingTrendsLineChart.setData(reportDao.getBorrowingTrendsData());
        borrowingTrendsLineChart.setTitle("Borrowing Trends (Last Week)");

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

    public void handleLogout(ActionEvent event) throws IOException {
        // Navigate to login page
        Parent loginRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(loginRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }
}
