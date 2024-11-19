package com.example.sneakershop3.Controllers;

import com.example.sneakershop3.dbConnection.connectionClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Label bl_customer;

    @FXML
    private Button btnAdd;

    @FXML
    private AnchorPane customer_form;

    @FXML
    private Label nameCustomer;

    @FXML
    private TextField nameCustomerLabel;

    @FXML
    private TextField phoneNumberLabel;

    @FXML
    private AnchorPane AnchorPane;

    @FXML
    private Label Customer2Label;

    @FXML
    private TableColumn<Customer, String> clo_numName;

    @FXML
    private TableColumn<Customer, String> clo_cusName;

    @FXML
    private Label menu_col_productName;

    @FXML
    private Label menu_clo_productName1;

    @FXML
    private TableView<Customer> menu_tableView;

    @FXML
    void menuShowData(ActionEvent event) {
    }

    @FXML
    private ScrollPane ScrollPane;

    @FXML
    private Label customerLabel;

    @FXML
    private AnchorPane scene1Pane;

    private ObservableList<Customer> customerData = FXCollections.observableArrayList();

    public void switchToScene1(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/sneakershop3/customer1.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToScene2(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/com/example/sneakershop3/customer2.fxml"));
        stage = (Stage) scene1Pane.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void displayCustomerInfo(String id, String name) {
        customerLabel.setText("ID: " + id + ", Name: " + name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (clo_cusName != null && clo_numName != null) {
            clo_cusName.setCellValueFactory(new PropertyValueFactory<>("cus_phoneNumber"));
            clo_numName.setCellValueFactory(new PropertyValueFactory<>("cus_name"));
            menu_tableView.setItems(customerData);
            loadCustomerData();
        } else {
            System.err.println("Table columns are not initialized properly.");
        }
    }

    private void loadCustomerData() {
        String query = "SELECT cus_phoneNumber, cus_name FROM customer";

        try (Connection conn = connectionClass.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            customerData.clear();

            while (resultSet.next()) {
                String phoneNumber = resultSet.getString("cus_phoneNumber");
                String name = resultSet.getString("cus_name");
                customerData.add(new Customer(phoneNumber, name));
                System.out.println("Loaded customer: " + phoneNumber + ", " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertCustomer(ActionEvent event) throws IOException {
        // Kiểm tra nếu trường tên khách hàng trống
        if (nameCustomerLabel.getText().isEmpty()) {
            showAlert("Warning", "Please enter the customer's name before adding.");
            return;
        }

        // Kiểm tra nếu trường số điện thoại trống
        if (phoneNumberLabel.getText().isEmpty()) {
            showAlert("Warning", "Please enter the phone number before adding.");
            return;
        }

        // Kiểm tra nếu số điện thoại đã tồn tại trong cơ sở dữ liệu
        String checkQuery = "SELECT * FROM customer WHERE cus_phoneNumber = ?";
        try (Connection conn = connectionClass.getConnection();
             PreparedStatement checkStatement = conn.prepareStatement(checkQuery)) {

            checkStatement.setString(1, phoneNumberLabel.getText());
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // Số điện thoại đã tồn tại
                showAlert("Error", "The phone number already exists in the system.");
                return;
            } else {
                // Số điện thoại chưa tồn tại, tiếp tục thêm thông tin khách hàng
                String insertQuery = "INSERT INTO customer (cus_phoneNumber, cus_name) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, phoneNumberLabel.getText());
                    preparedStatement.setString(2, nameCustomerLabel.getText());

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Customer added successfully!");
                        showAlert("Success", "Customer added successfully!");
                        ActionEvent actionEvent = new ActionEvent();
                        switchToScene2(actionEvent);
                    }
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Error: Customer with this phone number already exists!");
                showAlert("Error", "The user already exists");
            } else {
                e.printStackTrace();
            }
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleRowSelect(MouseEvent mouseEvent) throws IOException {
        String customerId = nameCustomerLabel.getText();
        String customerName = phoneNumberLabel.getText();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sneakershop3/customer2.fxml"));
        Parent root = loader.load();


        Label customerInfoLabel = (Label) root.lookup("#customerInfoLabel"); // Ensure ID in customer2.fxml is #customerInfoLabel
        if (customerInfoLabel != null) {
            customerInfoLabel.setText("Customer ID: " + customerId + ", Name: " + customerName);
        }

        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void backSales(ActionEvent event) throws IOException {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sneakershop3/sales.fxml"));
            Parent root = loader.load();
            Stage stage = null;
            if (event.getSource() instanceof Node) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }
            if (stage != null) {
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                System.err.println("Unable to find Stage.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
