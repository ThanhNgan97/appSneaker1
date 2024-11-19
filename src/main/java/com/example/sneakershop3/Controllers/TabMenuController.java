package com.example.sneakershop3.Controllers;

import com.example.sneakershop3.Models.TopSale;
import com.example.sneakershop3.dbConnection.connectionClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class TabMenuController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayTodaySales();
        displayNumberCustomers();
        displayTotalSales();
        displayIncomeChart();
        loadTopSalesData();
    }

    @FXML
    private Button dashboard_btn;

    @FXML
    private Label customers_lbl;

    @FXML
    private Button products_btn;

    @FXML
    private Label total_lbl;

    @FXML
    private Button sales_btn;

    @FXML
    private Label sales_lbl;

    @FXML
    private Button signout_btn;

    @FXML
    private TableView<TopSale> top_sale_table;

    @FXML
    private TableColumn<TopSale, String> dashboard_col_productName;

    @FXML
    private TableColumn<TopSale, Integer> dashboard_col_totalSale;

    @FXML
    private BarChart<?, ?> income_chart;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void signoutBtn(ActionEvent event) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Sign Out Confirm");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure to sign out?");
        ButtonType result = confirmationAlert.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sneakershop3/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) signout_btn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Cannot turn back to the Login Screen.");
            }
        }
    }


    public void displayTodaySales() {
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        String sql = "SELECT SUM(rec_totalAmount) FROM receipt WHERE DATE(rec_date) = ?";

        connect = connectionClass.getConnection();
        try {
            double ti = 0;
            prepare = connect.prepareStatement(sql);
            prepare.setDate(1, sqlDate); // Đặt giá trị tham số ngày
            result = prepare.executeQuery();

            if (result.next()) {
                ti = result.getDouble(1); // Lấy kết quả theo chỉ số cột
            }
            sales_lbl.setText("$" + ti);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayNumberCustomers() {
        String sql = "SELECT COUNT(cus_phoneNumber) FROM customer";
        connect = connectionClass.getConnection();

        try {
            int nc = 0;
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            if (result.next()) {
                nc = result.getInt("COUNT(cus_phoneNumber)");
            }
            customers_lbl.setText(String.valueOf(nc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayTotalSales() {
        String sql = "SELECT SUM(rec_totalAmount) FROM receipt";

        connect = connectionClass.getConnection();
        try {
            double ti = 0;
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            if (result.next()) {
                ti = result.getDouble("SUM(rec_totalAmount)");
            }
            total_lbl.setText("$" + ti);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayIncomeChart(){
        income_chart.getData().clear();
        String sql = "SELECT DATE(rec_date), SUM(rec_totalAmount) FROM receipt GROUP BY rec_date ORDER BY TIMESTAMP(rec_date)";
        connect = connectionClass.getConnection();
        XYChart.Series chart = new XYChart.Series();
        List<XYChart.Data<String, Number>> tempList = new ArrayList<>();
        try{
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                tempList.add(new XYChart.Data<>(result.getString(1), result.getDouble(2)));
            }

            int startIndex = Math.max(0, tempList.size() - 7);
            for (int i = startIndex; i < tempList.size(); i++) {
                chart.getData().add(tempList.get(i));
            }
            income_chart.getData().add(chart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTopSalesData() {
        String sql = "SELECT pd.prod_name, SUM(rd.recd_quantity) AS total_sale " +
                "FROM receiptDetails rd " +
                "JOIN productDetails pd ON rd.prod_barCode = pd.prod_barCode " +
                "GROUP BY pd.prod_name " +
                "ORDER BY total_sale DESC";

        ObservableList<TopSale> topSaleList = FXCollections.observableArrayList();

        connect = connectionClass.getConnection();
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();


            while (result.next()) {
                String productName = result.getString("prod_name");
                int totalSale = result.getInt("total_sale");
                topSaleList.add(new TopSale(productName, totalSale));
            }

            dashboard_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
            dashboard_col_totalSale.setCellValueFactory(new PropertyValueFactory<>("totalSale"));

            top_sale_table.setItems(topSaleList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleProductPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sneakershop3/Product.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) products_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open FXML file.");
        }
    }

    public void handleSalePage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sneakershop3/sales.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sales_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open FXML file.");
        }
    }
}
