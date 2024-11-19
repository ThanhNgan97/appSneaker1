package com.example.sneakershop3.Controllers;

import com.example.sneakershop3.Models.ProductsData;
import com.example.sneakershop3.dbConnection.connectionClass;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    public Button dashboard_btn;
    public Button products_btn;
    public Button sales_btn;
    public Button signout_btn;

    @FXML
    private Button btnadd;

    @FXML
    private Button btnclear;

    @FXML
    private Button btndelete;

    @FXML
    private Button btnimport;

    @FXML
    private Button btnupdate;

    @FXML
    private ComboBox<String> cbbcategory;

    @FXML
    private ComboBox<Integer> cbbsize;

    @FXML
    private ComboBox<String> cbbunit;

    @FXML
    private ComboBox<String> cbbbrand;

    @FXML
    private ImageView imageview;

    @FXML
    private Label lbbrand;

    @FXML
    private Label lbcategory;

    @FXML
    private Label lbprice;

    @FXML
    private Label lbproductcode;

    @FXML
    private Label lbproductname;

    @FXML
    private Label lbsize;

    @FXML
    private Label lbstock;

    @FXML
    private Label lbunit;

    @FXML
    private TableView<ProductsData> product_tableview;

    @FXML
    private TableColumn<ProductsData, String> prodct_col_productcode;

    @FXML
    private TableColumn<ProductsData, String> product_col_productname;

    @FXML
    private TableColumn<ProductsData, String> product_col_unit;

    @FXML
    private TableColumn<ProductsData, Double> product_col_price;

    @FXML
    private TableColumn<ProductsData, String> product_col_brand;

    @FXML
    private TableColumn<ProductsData, Integer> product_col_size;

    @FXML
    private TableColumn<ProductsData, Integer> product_col_stock;

    @FXML
    private TableColumn<ProductsData, String> product_col_category;

    @FXML
    private TableColumn<ProductsData, String> product_col_date;

    @FXML
    private TextField tfbrand;

    @FXML
    private TextField tfprice;

    @FXML
    private TextField tfproductcode;

    @FXML
    private TextField tfproductname;

    @FXML
    private TextField tfstock;

    @FXML
    private TextField tfunit;

    private ObservableList<ProductsData> productList = FXCollections.observableArrayList();

    private static final Map<Integer, Integer> categoryMap = new HashMap<>();

    static {
        for (int i = 0; i <= 14; i++) {
            categoryMap.put(30 + i, i + 1);
        }
    }

    private int getSizeIdFromName(int cateName) {
        return categoryMap.getOrDefault(cateName, 0);
    }

    // Phương thức hiển thị thông báo thành công
    private void showSuccessAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Add product successfully!");
        alert.showAndWait();
    }

    // Phương thức hiển thị thông báo lỗi
    private void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Cannot add product!: " + errorMessage);
        alert.showAndWait();
    }

    @FXML
    public void handleUpdateProduct() {
        // Lấy sản phẩm được chọn từ TableView
        ProductsData selectedProduct = product_tableview.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showErrorAlert("Please select a product to update.");
            return;
        }

        // Lấy dữ liệu từ các trường nhập liệu
        String barcode = tfproductcode.getText();
        String name = tfproductname.getText();
        String unit = cbbunit.getValue();
        String priceText = tfprice.getText();
        String stockText = tfstock.getText();
        String imagePath = imageview.getImage() != null ? imageview.getImage().getUrl() : "";
        if (barcode.isEmpty() || name.isEmpty() || unit == null || priceText.isEmpty() ||
                cbbcategory.getValue() == null || cbbbrand.getValue() == null || cbbsize.getValue() == null) {
            showErrorAlert("Please fill in all information.");
            return;
        }

        // Chuyển đổi dữ liệu từ TextField sang các kiểu phù hợp
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showErrorAlert("Please enter the price as a number.");
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            showErrorAlert("Please enter inventory quantity as an integer.");
            return;
        }

        int categoryId = switch (cbbcategory.getValue()) {
            case "Men" -> 1;
            case "Women" -> 2;
            case "Kids" -> 3;
            default -> -1;
        };

        if (categoryId == -1) {
            showErrorAlert("Invalid category.");
            return;
        }

        String brand = cbbbrand.getValue();
        int size = cbbsize.getValue();

        int size_Id = getSizeIdFromName(size);

        // Cập nhật dữ liệu cho sản phẩm được chọn
        selectedProduct.setProd_name(name);
        selectedProduct.setProd_unit(unit);
        selectedProduct.setProd_price(price);
        selectedProduct.setProd_image(imagePath);
        selectedProduct.setCate_Id(categoryId);
        selectedProduct.setProd_brand(brand);
        selectedProduct.setSize_name(size);
        selectedProduct.setWare_stock(stock);

        // Cập nhật cơ sở dữ liệu
        boolean isUpdated = selectedProduct.updateProductInDatabase(size_Id);

        if (isUpdated) {
            Alert successAlert = new Alert(AlertType.INFORMATION);
            successAlert.setTitle("Information");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Product update successful!");
            successAlert.showAndWait();

            loadData();  // Tải lại dữ liệu để hiển thị chính xác
            product_tableview.refresh();  // Đây là bước làm mới TableView

            clearFields();
        } else {
            showErrorAlert("Unable to update product.");
        }
    }

    @FXML
    public void handleDeleteProduct() {
        // Lấy sản phẩm được chọn từ TableView
        ProductsData selectedProduct = product_tableview.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            System.out.println("Please choose a product to delete.");
            return;
        }

        // Lấy size_Id từ sản phẩm được chọn
        int sizeIdToDelete = selectedProduct.getSize_Id();

        // Hiển thị một hộp thoại xác nhận trước khi xóa
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm delete");
        confirmationAlert.setHeaderText("Are you sure to delete this product?");
        confirmationAlert.setContentText("Product's Barcode: " + selectedProduct.getProd_barCode() + " and Size: " + sizeIdToDelete);

        if (confirmationAlert.showAndWait().get() == ButtonType.OK) {
            // Xóa sản phẩm khỏi cơ sở dữ liệu
            boolean isDeleted = selectedProduct.deleteProductFromDatabase(sizeIdToDelete);

            if (isDeleted) {
                System.out.println("The product has been deleted from the database.");
                productList.remove(selectedProduct);
                product_tableview.refresh();
            } else {
                System.out.println("Unable to delete the product from the database.");
            }
        }
    }

    public void moveImage(File file, String desiredDestination){
        File desiredFolder = new File(desiredDestination);

        Path desiredPath = desiredFolder.toPath().resolve(file.getName());

        try {
            Files.copy(file.toPath(), desiredPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File to moved to: " + desiredPath.toString());
        } catch (IOException e){
            System.out.println("Failed");
            e.printStackTrace();
        }
    }

    String imageForImporting = null;
    @FXML
    private void handleBtnImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(btnimport.getScene().getWindow());
        String targetPath = "D:\\Code\\Java\\SneakerShop3 - Copy\\src\\main\\resources\\com\\example\\sneakershop3\\Image\\";
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            System.out.println(selectedFile.getName());
            imageForImporting = selectedFile.getName();
            moveImage(selectedFile, targetPath);
            imageview.setImage(image);
        }
    }

    public void brandListComboBox() throws SQLException {
        ObservableList<String> brandList = FXCollections.observableArrayList();
        Connection connection = connectionClass.getConnection();
        String productdetails_mysql = "SELECT DISTINCT prod_brand FROM productdetails";
        PreparedStatement preparedStatement = connection.prepareStatement(productdetails_mysql);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String brand = resultSet.getString("prod_brand");
            brandList.add(brand);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        cbbbrand.setItems(brandList);
    }

    @FXML
    private void showSelectedProductDetails() {
        ProductsData selectedProduct = product_tableview.getSelectionModel().getSelectedItem();

        // Kiểm tra nếu không có sản phẩm nào được chọn
        if (selectedProduct == null) return;

        // Đảm bảo tất cả các trường không bị null trước khi gán giá trị
        if (tfproductcode != null) tfproductcode.setText(selectedProduct.getProd_barCode());
        if (tfproductname != null) tfproductname.setText(selectedProduct.getProd_name());
        if (tfprice != null) tfprice.setText(Double.toString(selectedProduct.getProd_price()));
        if (tfstock != null) tfstock.setText(Integer.toString(selectedProduct.getWare_stock()));

        // Gán giá trị cho các ComboBox
        if (cbbunit != null) cbbunit.setValue(selectedProduct.getProd_unit());
        if (cbbcategory != null) cbbcategory.setValue(selectedProduct.getCategoryName());
        if (cbbbrand != null) cbbbrand.setValue(selectedProduct.getProd_brand());
        if (cbbsize != null) {
            try {
                cbbsize.setValue(selectedProduct.getSize_name());
            } catch (NumberFormatException e) {
                cbbsize.setValue(null); // Gán null nếu không thể chuyển đổi
            }
        }

        // Kiểm tra URL hình ảnh trước khi tải
        String imageUrl = selectedProduct.getProd_image();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                System.out.println("sisdxs: " + imageUrl);
                Image image = new Image(getClass().getResourceAsStream("/com/example/sneakershop3/Image/" + imageUrl));
                if (image.isError()) {
                    System.out.println("Invalid image URL or resource not found.");
                    // Đặt hình ảnh mặc định nếu URL không hợp lệ
                    imageview.setImage(new Image("file:/Users/anhthu/Documents/Zalo%20Received%20Files/SneakerShop3%20-%20Copy/src/main/resources/com/example/sneakershop3/Image/error.jpg"));
                } else {
                    imageview.setImage(image);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid image URL or resource not found.");
                imageview.setImage(new Image("file:/Users/anhthu/Documents/Zalo%20Received%20Files/SneakerShop3%20-%20Copy/src/main/resources/com/example/sneakershop3/Image/error.jpg")); // Đặt hình ảnh mặc định nếu xảy ra ngoại lệ
            }
        } else {
            imageview.setImage(new Image("file:/Users/anhthu/Documents/Zalo%20Received%20Files/SneakerShop3%20-%20Copy/src/main/resources/com/example/sneakershop3/Image/error.jpg")); // Đặt hình ảnh mặc định nếu URL rỗng hoặc null
        }
    }

    @FXML
    public void handleAddProduct() {
        Map<String, Integer> categoryMap = Map.of(
                "Men", 1,
                "Women", 2,
                "Kids", 3
        );

        String barcode = tfproductcode.getText();
        String name = tfproductname.getText();
        String unit = cbbunit.getValue();
        String priceText = tfprice.getText();
        String imagePath = imageview.getImage() != null ? imageview.getImage().getUrl() : "";
        String stockText = tfstock.getText();

        // Check input information
        if (barcode.isEmpty() || name.isEmpty() || unit == null || priceText.isEmpty() ||
                cbbcategory.getValue() == null || cbbbrand.getValue() == null ||
                cbbsize.getValue() == null || imagePath.isEmpty()) {
            showErrorAlert("Please fill in all the information.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showErrorAlert("Please enter a valid number for the product price.");
            return;
        }

        int categoryId = categoryMap.getOrDefault(cbbcategory.getValue(), -1);
        if (categoryId == -1) {
            showErrorAlert("Invalid category.");
            return;
        }

        String brand = cbbbrand.getValue();
        int size = cbbsize.getValue();

        int sizeId = getSizeIdFromName(size);

        int stock;
        try {
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            showErrorAlert("Please enter an integer for the stock quantity.");
            return;
        }

        String currentTime = java.time.LocalDateTime.now().toString();
        ProductsData newProduct = new ProductsData(barcode, name, unit, price, imagePath, categoryId, brand, sizeId, size, stock, currentTime);

        // Add product to the database
        boolean isAdded = newProduct.addProductToDatabase(imageForImporting);
        Stage stage = (Stage) btnadd.getScene().getWindow();
        if (isAdded) {
            showSuccessAlert();
            newProduct.setWare_lastUpdate(currentTime);
            productList.add(newProduct);
            product_tableview.refresh();
            clearFields();
        } else {
            showErrorAlert("Unable to add the product to the database.");
        }
    }

    // Phương thức xóa các trường nhập liệu
    private void clearFields() {
        tfproductcode.clear();
        tfproductname.clear();
        tfprice.clear();
        tfstock.clear();
        cbbcategory.setValue(null);
        cbbbrand.setValue(null);
        cbbsize.setValue(null);
        imageview.setImage(null);
    }

    private void loadData() {
        productList.clear(); // Xóa dữ liệu cũ trước khi tải lại
        Connection connection = connectionClass.getConnection();
        String query = "SELECT wd.prod_barCode,pd.prod_name,c.cate_name,pd.prod_unit,pd.prod_price, pd.prod_image, c.cate_Id,pd.prod_brand,s.size_name, s.size_Id,wd.stock_quantity,w.ware_lastUpdate " +
                " FROM warehousedetails wd " +
                "         JOIN productdetails pd ON wd.prod_barCode = pd.prod_barCode " +
                "         JOIN category c ON pd.cate_Id = c.cate_Id " +
                "         JOIN size s ON wd.size_Id = s.size_Id " +
                "         JOIN warehouse w ON wd.ware_Id = w.ware_Id;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String barcode = resultSet.getString("prod_barCode");
                String name = resultSet.getString("prod_name");
                String unit = resultSet.getString("prod_unit");
                double price = resultSet.getDouble("prod_price");
                String imagePath = resultSet.getString("prod_image");
                int categoryId = resultSet.getInt("cate_Id");
                String brand = resultSet.getString("prod_brand");
                int size = resultSet.getInt("size_name");
                int sizeId = resultSet.getInt("size_Id");
                int stock = resultSet.getInt("stock_quantity");
                String lastUpdate = resultSet.getString("ware_lastUpdate");

                ProductsData product = new ProductsData(barcode, name, unit, price, imagePath, categoryId, brand, sizeId, size, stock, lastUpdate);
                productList.add(product);
            }
            product_tableview.setItems(productList);
            product_tableview.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        cbbcategory.getItems().addAll("Men", "Women", "Kids");

        cbbcategory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            cbbsize.getItems().clear();

            if (newValue != null) {
                switch (newValue) {
                    case "Men" -> cbbsize.getItems().addAll(39, 40, 41, 42, 43, 44);
                    case "Women" -> cbbsize.getItems().addAll(36, 37, 38, 39, 40);
                    case "Kids" -> cbbsize.getItems().addAll(30, 31, 32, 33, 34, 35);
                }
            }
        });
        cbbunit.getItems().addAll("Pair");

        try {
            brandListComboBox();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        btnimport.setOnAction(event -> handleBtnImport());

        prodct_col_productcode.setCellValueFactory(new PropertyValueFactory<>("prod_barCode"));
        product_col_productname.setCellValueFactory(new PropertyValueFactory<>("prod_name"));
        product_col_unit.setCellValueFactory(new PropertyValueFactory<>("prod_unit"));
        product_col_price.setCellValueFactory(new PropertyValueFactory<>("prod_price"));
        product_col_brand.setCellValueFactory(new PropertyValueFactory<>("prod_brand"));
        product_col_size.setCellValueFactory(new PropertyValueFactory<>("size_name"));
        product_col_stock.setCellValueFactory(new PropertyValueFactory<>("ware_stock"));
        product_col_category.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategoryName())
        );

        product_col_date.setCellValueFactory(new PropertyValueFactory<>("ware_lastUpdate"));

        loadData();
        btnclear.setOnAction(event -> clearFields());
        product_tableview.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showSelectedProductDetails();
        });

        btndelete.setOnAction(event -> handleDeleteProduct());
        btnupdate.setOnAction(event -> handleUpdateProduct());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void handleDashboardPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sneakershop3/TabMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dashboard_btn.getScene().getWindow();
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

    public void signoutBtn(ActionEvent event) {
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
}