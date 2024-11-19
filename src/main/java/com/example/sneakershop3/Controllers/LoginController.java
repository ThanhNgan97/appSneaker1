package com.example.sneakershop3.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private Button login_btn;

    @FXML
    private PasswordField password_fld;

    @FXML
    void loginBtn(ActionEvent event) {
        String password = password_fld.getText();

        if (password.isEmpty()) {
            showAlert("Error", "Please enter a password.");
        } else if (!password.equals("123456")) {
            showAlert("Error", "Invalid password.");
            password_fld.clear();
        } else {
            try {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Login Successful");
                alert.showAndWait();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sneakershop3/TabMenu.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) login_btn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Cannot open FXML file.");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
