
package com.example.sneakershop3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml")); // Adjusted path if needed
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e){
            System.err.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}

