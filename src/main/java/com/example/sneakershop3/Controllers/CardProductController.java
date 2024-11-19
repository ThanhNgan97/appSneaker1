
package com.example.sneakershop3.Controllers;

import com.example.sneakershop3.Models.ProductData;
import com.example.sneakershop3.dbConnection.connectionClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CardProductController implements Initializable {
    @FXML
    private AnchorPane card_form;

    @FXML
    private Button prod_addBtn;

    @FXML
    public ComboBox<Integer> prod_comboBox_size;

    @FXML
    private ImageView prod_imageView;

    @FXML
    private Label prod_name;

    @FXML
    private Label prod_price;

    @FXML
    private Spinner<Integer> prod_spinner_quantity;

    @FXML
    private ComboBox<Integer> cbb2;

    @FXML
    ComboBox<String> categoryComboBox;

    @FXML
    private Label prod_quantityLabel;

    public ComboBox<Integer> getProd_comboBox_size() {
        return prod_comboBox_size;
    }

    private SalesController salesController;

    public SalesController getSalesController() {
        return salesController;
    }

    public void setProd_comboBox_size(ComboBox<Integer> prod_comboBox_size) {
        this.prod_comboBox_size = prod_comboBox_size;
    }

    private String category;

    public void setCategory(String category) {
        this.category = category;
        System.out.println("Category set in CardProductController: " + this.category);
    }

    private int wareQuantity;

    private ProductData proData;

    private String barCodeForQuantity = "";

    private String barCodeForWarehouse = "";

    public void setBarCodeForWarehouse(String barCodeForWarehouse) {
        this.barCodeForWarehouse = barCodeForWarehouse;
    }

    public void setBarCodeForQuantity(String barCodeForQuantity) {
        this.barCodeForQuantity = barCodeForQuantity;
    }

    public void setSalesController(SalesController salesController) {
        this.salesController = salesController;
        if (this.salesController != null) {
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prod_addBtn.setOnAction(event -> {
            try {
                addProduct(event);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        setCategoryComboBoxData();
        loadSizes(); // Gọi phương thức loadSizes để tải dữ liệu kích thước
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        prod_spinner_quantity.setValueFactory(valueFactory);
    }


    public void setCategoryComboBoxData() {
        System.out.println("Category set");
        if (prod_comboBox_size != null) {
        } else {
            System.out.println("ComboBox is not initialized. Please check the FXML file.");
        }
    }

    public String globalBarcode = "";

    public String getGlobalBarcode() {
        return globalBarcode;
    }

    public void setGlobalBarcode(String globalBarcode) {
        this.globalBarcode = globalBarcode;
    }

    public void setData(ProductData proData) throws SQLException {
        this.proData = proData; // Set the product data first
        String categoryName = this.category;
        setGlobalBarcode(proData.getProd_barCode());
        System.out.println("Category name: " + categoryName);

        System.out.println("Setting data for product: " + proData.getProd_name());

        // Kiểm tra và in ra đường dẫn hình ảnh
        String imagePath = "/com/example/sneakershop3/Image/" + proData.getProd_image();
        System.out.println("Image path: " + imagePath);

        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream == null) {
            System.err.println("Image file not found at path: " + imagePath);
        } else {
            try {
                Image image = new Image(imageStream);
                prod_imageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }

        // Set thông tin sản phẩm lên các component khác
        prod_name.setText(proData.getProd_name());
        prod_price.setText(String.valueOf(proData.getProd_price()));
        setBarCodeForWarehouse(proData.getProd_barCode());

        try {
            this.wareQuantity = getWareQuantity();
        } catch (SQLException e) {
            e.printStackTrace();
            this.wareQuantity = 0;
        }

        System.out.println("Category Name: " + categoryName);
        prod_comboBox_size.getItems().clear(); // Clear existing items each time data is set

        // Thiết lập các kích thước cho ComboBox theo danh mục sản phẩm
        if (categoryName != null) {
            switch (categoryName) {
                case "Men":
                    prod_comboBox_size.getItems().addAll(39, 40, 41, 42, 43, 44);
                    break;
                case "Women":
                    prod_comboBox_size.getItems().addAll(36, 37, 38, 39, 40);
                    break;
                case "Kids":
                    prod_comboBox_size.getItems().addAll(30, 31, 32, 33, 34, 35);
                    break;
                default:
                    System.out.println("Unknown category: " + categoryName);
                    break;
            }
        }

        // Thiết lập giá trị cho Spinner nếu số lượng trong kho lớn hơn 0
        if (wareQuantity > 0) {
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, wareQuantity, 1);
            prod_spinner_quantity.setValueFactory(valueFactory);
        }
    }

    public void updateSizeOptions(String categoryName) {
        prod_comboBox_size.getItems().clear(); // Xóa các kích thước cũ
        if (categoryName != null) {
            // Thêm kích thước dựa trên category
        }
    }

    private void updateProductDetails(int quantity) throws SQLException {
        Connection connection = connectionClass.getConnection();

        // Câu truy vấn UPDATE để cập nhật số lượng sản phẩm
        String sqlUpdate = "UPDATE warehousedetails SET stock_quantity = ? WHERE prod_barCode = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
        preparedStatement.setInt(1, quantity);                  // Số lượng sản phẩm cần cập nhật
        preparedStatement.setString(2, barCodeForWarehouse);    // Barcode sản phẩm cần cập nhật

        try {
            int rowsAffected = preparedStatement.executeUpdate();  // Thực thi câu lệnh SQL
            if (rowsAffected > 0) {
                System.out.println("Data successfully updated in productdetails.");
            } else {
                System.out.println("Failed to update data in productdetails.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadSizes() {
        ObservableList<Integer> sizes = FXCollections.observableArrayList();

        try {
            Connection connection = connectionClass.getConnection();
            String query = "SELECT size_name FROM size"; // Truy vấn cột size_name thay vì size_id
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                sizes.add(resultSet.getInt("size_name")); // Lấy size_name và thêm vào danh sách
            }

            prod_comboBox_size.setItems(sizes); // Gán danh sách kích thước vào ComboBox
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
public void addProduct(ActionEvent event) throws SQLException {

    int selectedQuantity = prod_spinner_quantity.getValue();
    int availableQuantity = getWareQuantityByBarCode(barCodeForWarehouse);

    if (salesController != null) {
        ProductData selectedProduct = this.proData;
        Integer selectedSize = prod_comboBox_size.getValue();

        if (selectedSize == null) {
            showAlert("Invalid Size", "Please select a product size.");
            return;
        }

        if (selectedQuantity > availableQuantity) {
            showAlert("Out of Stock", "Requested quantity exceeds available stock!");
        } else if (selectedQuantity == 0) {
            showAlert("Invalid Quantity", "Please select a quantity greater than 0.");
        } else {

            salesController.addProductToSale(selectedProduct, selectedSize, selectedQuantity);
            showAlert("Product Added", "The product has been added successfully!");
        }
    } else {
        System.err.println("SalesController reference is null.");
    }
}

    public int getWareQuantityByBarCode(String barCode) throws SQLException {
        Connection connection = connectionClass.getConnection();
        int currentWareQuantity = 0;
        String sqlQuery = "SELECT stock_quantity FROM warehousedetails where prod_barCode = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, barCode);
        System.out.println("getWareQuantityByBarCode: Barcode needed: " + barCode);

        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                currentWareQuantity = resultSet.getInt("stock_quantity");
                System.out.println("getWareQuantityByBarCode: " + currentWareQuantity);
            }
        } catch (SQLException e) {
            System.out.println("Cannot retrieve data");
        }

        return currentWareQuantity;
    }

    public int getWareQuantity() throws SQLException {
        Connection connection = connectionClass.getConnection();
        int currentWareQuantity = 0;
        String sqlQuery = "SELECT stock_quantity FROM warehousedetails where prod_barCode = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, barCodeForQuantity);
        System.out.println("Barcode needed: " + barCodeForQuantity);

        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                currentWareQuantity = resultSet.getInt("ware_stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return currentWareQuantity;
    }

    // Display alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

