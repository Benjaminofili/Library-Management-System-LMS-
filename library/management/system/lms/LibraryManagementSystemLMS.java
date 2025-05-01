/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.management.system.lms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author benja
 */
public class LibraryManagementSystemLMS extends Application {
    
  @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Library Management System");

        // 👉 Apply the dimensions here
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setMaxWidth(1440);
        primaryStage.setMaxHeight(1024);
//        primaryStage.setResizable(false); // Optional

        primaryStage.show();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
