package com.example.sneakershop3.Controllers;

import com.example.sneakershop3.Models.CustomerData;
import com.example.sneakershop3.Report.pdfReceiptGenerator;
import javafx.css.CssParser;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import java.sql.*;
import java.sql.Statement;
import com.example.sneakershop3.dbConnection.connectionClass;
import com.example.sneakershop3.Models.ProductData;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SalesController implements Initializable {
    public Button dashboard_btn;
    public Button products_btn;
    public Button sales_btn;
    public Button signout_btn;

    @FXML
    private Label menu_amount;

    @FXML
    private Label menu_change;

    @FXML
    private Label menu_col_price;

    @FXML
    private Label menu_col_productName;

    @FXML
    private Label menu_col_quantity;

    @FXML
    private AnchorPane menu_form;

    @FXML
    private GridPane menu_gridPane;

    @FXML
    private Button menu_payNowBtn;

    @FXML
    private Button menu_receiptBtn;

    @FXML
    private Button menu_removeBtn;

    @FXML
    private ScrollPane menu_scrollPane;

    @FXML
    private TableView<ProductData> menu_tableView;

    @FXML
    private Label menu_total;

    @FXML
    public ComboBox<String> categoryComboBox;

    @FXML
    private ComboBox<String> brandComboBox;

    @FXML
    private GridPane GridPane;

    @FXML
    private Label customerInfoLabel; // Để hiển thị thông tin khách hàng trên customer1.fxml

    @FXML
    private TextField textfieldAmount;

    @FXML
    private  Label changesPrice;

    @FXML
    private Label totalPrice;

    @FXML
    private TableColumn<ProductData, Integer> colSize;

    @FXML
    private ComboBox<Integer> prod_comboBox_size;

    @FXML
    private TextField textfield_search;

    @FXML
    private TextArea messageBox;

    //add database in tableView
    @FXML
    private TableColumn<Customer, Integer> clo_numName;

    @FXML
    private TableColumn<Customer, String> clo_cusName;

    @FXML
    private TextField textfieldphoneNumber;

    @FXML
    private Label menu_phoneNumber;

    private int getid;
    private double totalP;
    private double amount;
    private double change;
    private int cID;

    private ObservableList<CustomerData> customersListData;

    @FXML
    private TextField txtBarcode;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Image image;

    @FXML
    private TableColumn<ProductData, String> colProductName;

    @FXML
    private TableColumn<ProductData, Integer> colQuantity;

    @FXML
    private TableColumn<ProductData, Double> colPrice;

    private ObservableList<ProductData> productList = FXCollections.observableArrayList();

    //hien cardProduct
    @FXML
    void menuShowData(ActionEvent event) {
        System.out.println("menuShowData event triggered!");
    }

    public void brandListCoboBox() throws SQLException {
        ObservableList<String> brandList = FXCollections.observableArrayList(); // Danh sách để lưu các thương hiệu
        Connection connection = connectionClass.getConnection();
        String productdetails_mysql = "SELECT DISTINCT prod_brand FROM productdetails";
        PreparedStatement preparedStatement = connection.prepareStatement(productdetails_mysql);
        ResultSet resultSet = preparedStatement.executeQuery();

        //thêm vào danh sách
        while (resultSet.next()) {
            String brand = resultSet.getString("prod_brand");
            brandList.add(brand);
        }

        // Đóng kết nối
        resultSet.close();
        preparedStatement.close();
        connection.close();

        // Thêm danh sách vào ComboBox
        brandComboBox.setItems(brandList);
    }

    public void switchToScene2(ActionEvent event) throws IOException {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sneakershop3/customer2.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //xoa san pham
    public void removeBtn(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to delete all items?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            menu_tableView.getItems().clear();

            totalPrice.setText("$0.0");
            textfieldAmount.setText("");
            changesPrice.setText("$0.0");
        } else {
            alert.close();
        }
    }

    public void categoryListComboBox() throws SQLException {
        ObservableList<String> categoryList = FXCollections.observableArrayList();
        Connection connection = connectionClass.getConnection();
        String categoryQuery = "SELECT DISTINCT cate_name FROM category"; // Adjust table/column names as needed

        try (PreparedStatement preparedStatement = connection.prepareStatement(categoryQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                categoryList.add(resultSet.getString("cate_name"));
            }
        } finally {
            if (connection != null) connection.close();
        }
        categoryComboBox.setItems(categoryList);
    }

    @FXML
    public void filterByBrand(ActionEvent event) {
        String selectedBrand = brandComboBox.getValue();
        ObservableList<ProductData> filteredList = FXCollections.observableArrayList();
        System.out.println("Brand choice");
        for (ProductData product : productList) {
            if (selectedBrand == null || selectedBrand.equals(product.getProd_brand())) {
                filteredList.add(product);
            }
        }

        // Update the TableView to show only filtered products
        menu_tableView.setItems(filteredList);
        menu_tableView.refresh();
    }

    public void brandListComboBox() throws SQLException {
        ObservableList<String> brandList = FXCollections.observableArrayList();
        Connection connection = connectionClass.getConnection();
        String brandQuery = "SELECT DISTINCT prod_brand FROM productdetails"; // Adjust table/column names as needed

        try (PreparedStatement preparedStatement = connection.prepareStatement(brandQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                brandList.add(resultSet.getString("prod_brand"));
            }
        } finally {
            if (connection != null) connection.close();
        }
        brandComboBox.setItems(brandList);
    }

    public ObservableList<ProductData> menuGetData() {
        String search = textfield_search.getText();
        // Câu truy vấn với JOIN để lấy size_name từ bảng size
        String sql = "select * from productdetails where prod_name like ?;";

        ObservableList<ProductData> listData = FXCollections.observableArrayList();

        try {
            Connection connection = connectionClass.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + search + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ProductData prod = new ProductData(
                        resultSet.getString("prod_barCode"),
                        resultSet.getString("prod_name"),
                        resultSet.getString("prod_unit"),
                        resultSet.getDouble("prod_price"),
                        resultSet.getString("prod_image"),
                        resultSet.getInt("cate_Id"),
                        resultSet.getString("prod_brand")
                );
                listData.add(prod);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listData;
    }

    private ObservableList<ProductData> cardListData = FXCollections.observableArrayList();

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public void menuDisplayCard(String selectedBrand) {
        this.cardListData.clear();
        this.cardListData.addAll(this.menuGetData());

        if (selectedBrand != null && !selectedBrand.isEmpty()) {
            this.cardListData.removeIf(product -> !product.getProd_brand().equals(selectedBrand));
        }

        int row = 0;
        int column = 0;
        menu_gridPane.getChildren().clear();
        menu_gridPane.getRowConstraints().clear();
        menu_gridPane.getColumnConstraints().clear();

        for (ProductData productData : this.cardListData) {
            try {
                FXMLLoader load = new FXMLLoader(getClass().getResource("/com/example/sneakershop3/cardItem.fxml"));
                AnchorPane pane = load.load();
                CardProductController cardC = load.getController();

                if (cardC != null) {

                    String selectedCategory = categoryComboBox.getValue();
                    cardC.setCategory(selectedCategory);
                    cardC.setData(productData);

                    cardC.setSalesController(this);
                } else {
                    System.err.println("CardProductController not loaded correctly.");
                }

                if (column == 3) {
                    column = 0;
                    row++;
                }
                menu_gridPane.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(10.0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //addButtonClick
    public void addProductToSale(ProductData proData, Integer size, int quantity) throws SQLException {
        Connection connection = connectionClass.getConnection();

        System.out.println("Adding product to cart: " + proData.getProd_name() + " - Size: " + size);
        barcodeForRecipt = proData.getProd_barCode();
        // Clone the ProductData to create a new entry
        ProductData newProductEntry = new ProductData(
                proData.getProd_barCode(),
                proData.getProd_name(),
                proData.getProd_unit(),
                proData.getProd_price(),
                proData.getProd_image(),
                size,
                quantity
        );

        boolean productFound = false;
        for (ProductData existingProduct : productList) {
            if (existingProduct.getProd_barCode().equals(proData.getProd_barCode()) &&
                    existingProduct.getProd_size().equals(size)) {
                existingProduct.setProd_quantity(existingProduct.getProd_quantity() + quantity);
                productFound = true;
                break;
            }
        }
        if (!productFound) {
            productList.add(newProductEntry);
        }
        menu_tableView.setItems(productList);
        menu_tableView.refresh();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = 0.0;
        for (ProductData product : menu_tableView.getItems()) {
            total += product.getProd_price()* product.getProd_quantity();
        }
        totalPrice.setText(String.format("$%.2f", total));
    }

    //tableView
    public void customerID() {
        String sql = "SELECT MAX(customer_id) FROM customer";
        Connection connection=connectionClass.getConnection();
        try {
            this.prepare = this.connect.prepareStatement(sql);
            this.result = this.prepare.executeQuery();
            if (this.result.next()) {
                this.cID = this.result.getInt("MAX(customer_id)");
            }

            String checkCID = "SELECT MAX(customer_id) FROM receipt";
            this.prepare = this.connect.prepareStatement(checkCID);
            this.result = this.prepare.executeQuery();
            int checkID = 0;

            if (this.result.next()) {
                checkID = this.result.getInt("MAX(customer_id)");
            }

            if (this.cID == 0) {
                ++this.cID;
            } else if (this.cID == checkID) {
                ++this.cID;
            }

            data.cID = this.cID;
        } catch (Exception var4) {
            Exception e = var4;
            e.printStackTrace();
        }
    }

    public void loadData() {
        List<ProductData> productList = fetchProductsFromDatabase();
        ObservableList<ProductData> observableProductList = FXCollections.observableArrayList(productList);
       menu_tableView.setItems(observableProductList);
    }

    private List<ProductData> fetchProductsFromDatabase() {
        List<ProductData> productList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionClass.getConnection();
            // Câu truy vấn SQL sử dụng INNER JOIN để kết hợp bảng productdetails với receiptdetails
            String sql = "SELECT pd.prod_barCode, pd.prod_name, pd.prod_price, pd.prod_image, rd.recd_quantity, s.size_name " +
                    "FROM productdetails pd " +
                    "JOIN receiptdetails rd ON pd.prod_barCode = rd.prod_barCode " + // Điều kiện JOIN thích hợp
                    "JOIN size s ON pd.size_id = s.size_id"; // Điều kiện JOIN bổ sung nếu cần liên kết với bảng size

            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String prodName = resultSet.getString("prod_name");
                Double prodPrice = resultSet.getDouble("prod_price");
                Integer prodSize = resultSet.getInt("size_name");
                Integer prodQuantity = resultSet.getInt("recd_quantity");

                // Tạo đối tượng ProductData với các dữ liệu cần thiết
                ProductData product = new ProductData(prodName, prodPrice, prodSize, prodQuantity);
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return productList;
    }

    public void searchProduct(String barcode) {
        ObservableList<ProductData> productList = menu_tableView.getItems(); // Assuming menu_tableView is your TableView

        if (productList == null) {
            productList = FXCollections.observableArrayList();
            menu_tableView.setItems(productList);
        }

        int quantity = 0, size = 0;
        String actualBarcode = null;
        if(barcode.length() > 13) {
            quantity = Integer.parseInt(barcode.substring(0, 2));
            size = Integer.parseInt(barcode.substring(2, 4));
            actualBarcode = barcode.substring(4);
            System.out.println("Quantity and size: "+ quantity + size);
        } else {
            actualBarcode = barcode;
        }

        Connection connection = connectionClass.getConnection();

        if (connection != null) {
            System.out.println("Connected to database for product search.");

            String sqlQuery = "SELECT * FROM productdetails WHERE prod_barCode = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, actualBarcode);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String productCode = resultSet.getString("prod_barCode");
                        String productName = resultSet.getString("prod_name");
                        String unit = resultSet.getString("prod_unit");
                        double price = resultSet.getDouble("prod_price");

                        boolean productFound = false;
                        for (ProductData product : productList) {
                            if (product.getProd_barCode().equals(productCode)) {
                                int currentQuantity = product.getProd_quantity();
                                product.setProd_quantity(currentQuantity + quantity);
                                productFound = true;
                                break;
                            }
                        }

                        if (!productFound) {
                            ProductData newProduct = new ProductData(
                                    productCode,
                                    productName,
                                    unit,
                                    price,
                                    size, // Set the size
                                    quantity // Set the quantity
                            );
                            productList.add(newProduct);
                            SalesController salesController = new SalesController();
                            System.out.println("Product added to list: " + newProduct.getProd_name());
                        }
                        menu_tableView.refresh();
                    } else {
                        System.out.println("No product found with the barcode: " + actualBarcode);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Connection to database failed.");
        }
    }

    private void calculateChange() {
        try {
            amount = Double.parseDouble(textfieldAmount.getText());
            double total = Double.parseDouble(totalPrice.getText().replace("$", ""));
            change = amount - total;
            if (change >= 0) {
                changesPrice.setText(String.format("$%.2f", change));
            } else {
                changesPrice.setText("Insufficient Amount");
            }
        } catch (NumberFormatException e) {
            changesPrice.setText("Invalid Amount");
        }
    }

    //thanh toan
    String barcodeForRecipt = "";
    public void payNowBtn(ActionEvent event) throws IOException, SQLException {

        // Kiểm tra xem trường Amount có trống hay không
        if (textfieldAmount.getText() == null || textfieldAmount.getText().isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Payment Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please enter an amount to proceed with the payment.");
            alert.showAndWait();
            return;
        }

        // Kiểm tra xem tableView có trống không
        if (menu_tableView.getItems().isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Payment Failed");
            alert.setHeaderText(null);
            alert.setContentText("Purchase unsuccessful. No items selected for purchase.");
            alert.showAndWait();
            return;
        }

        //kiểm tra xem đã nhập số điện thoại chưa
        String phoneNumber = textfieldphoneNumber.getText().trim();

        if (phoneNumber.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a phone number.");
            alert.showAndWait();
            return;
        }

        try {
            // Connect to the database
            Connection connection =connectionClass.getConnection(); // Use your database connection class
            String query = "SELECT * FROM customer WHERE cus_phoneNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, phoneNumber);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                // Phone number does not exist
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Notification");
                alert.setHeaderText(null);
                alert.setContentText("The phone number does not exist in the system.");
                alert.showAndWait();
            } else {
                // Tính toán thay đổi (change) từ tiền đã nhập
                calculateChange();

                // Nếu tiền đã nhập đủ
                if (change >= 0) {
                    // Lấy recId từ bảng receipt (thêm vào receiptdetails)
                    int recId = getLastInsertedRecId(); // Hàm để lấy rec_Id vừa được thêm vào bảng receipt

                    updateReceiptTotalAmount();
                    for (ProductData product : menu_tableView.getItems()) {
                        String prodBarCode = product.getProd_barCode();
                        int recdQuantity = product.getProd_quantity();

                        String query1 = "INSERT INTO receiptdetails (rec_Id, prod_barCode, recd_quantity) VALUES (?, ?, ?)";

                        try (Connection conn = connectionClass.getConnection();
                             PreparedStatement stmt = conn.prepareStatement(query1)) {

                            stmt.setInt(1, idForPDF);          // Chèn rec_Id
                            stmt.setString(2, prodBarCode); // Chèn mã vạch sản phẩm
                            stmt.setInt(3, recdQuantity);   // Chèn số lượng

                            // Thực thi câu lệnh SQL để chèn vào bảng receiptdetails
                            stmt.executeUpdate();
                            System.out.println("Receipt details inserted successfully.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Database Error");
                            alert.setHeaderText(null);
                            alert.setContentText("There was an error inserting receipt details.");
                            alert.showAndWait();
                        }
                    }

                    // Cập nhật số lượng sản phẩm trong kho
                    updateStock();

                    // Thông báo thanh toán thành công
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Payment Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("The transaction was completed successfully!");
                    alert.showAndWait();

                    // Hỏi người dùng có muốn in hóa đơn không
                    Alert printReceiptAlert = new Alert(AlertType.CONFIRMATION);
                    printReceiptAlert.setTitle("Print Receipt");
                    printReceiptAlert.setHeaderText(null);
                    printReceiptAlert.setContentText("Do you want to print the receipt?");

                    if (printReceiptAlert.showAndWait().get() == ButtonType.OK) {
                        printReceipt();
                    }

                    // Reset các trường Total, Amount, Change về giá trị mặc định (0)
                    totalPrice.setText("$0.00");
                    textfieldAmount.setText(" ");
                    changesPrice.setText("$0.00");

                    // Xóa dữ liệu trong tableView
                    menu_tableView.getItems().clear();
                } else {
                    // Thông báo nếu số tiền không đủ
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Payment Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Insufficient amount. Please enter a valid amount.");
                    alert.showAndWait();
                }

                // In ra thời gian hiện tại
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = currentDateTime.format(formatter);
                System.out.println("Current time: " + formattedDateTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while checking the phone number.");
            alert.showAndWait();
        }
    }

    private void printReceipt() {
        Stage stage = (Stage) menu_removeBtn.getScene().getWindow();
        pdfReceiptGenerator pdfReceiptGenerator = new pdfReceiptGenerator();
        pdfReceiptGenerator.pdfReceipt(stage, idForPDF);
    }

    // Phương thức cập nhật số lượng kho
    private void updateStock() throws SQLException {
        Connection connection = connectionClass.getConnection();

        // Loop through each product in the table and update the stock quantity
        for (ProductData product : menu_tableView.getItems()) {
            String currentBarCode = product.getProd_barCode(); // Product barcode
            int currentSizeId = product.getProd_size(); // Product size_id
            System.out.println("curSize: " + currentSizeId);
            int quantitySold = product.getProd_quantity(); // Quantity sold

            int currentStock = 0;
            int newStock;
            String getRightId = "SELECT size_Id FROM size where size_name = ?";
            PreparedStatement getRightIdStatement = connection.prepareStatement(getRightId);
            getRightIdStatement.setInt(1, currentSizeId);
            ResultSet rs2 = getRightIdStatement.executeQuery();
            if (rs2.next()) {
                currentSizeId = rs2.getInt(1);
            }
            System.out.println("New size id: " + currentSizeId);
            // Retrieve the current stock for the specific product and size
            String getCurrentStockQuery = "SELECT stock_quantity FROM warehousedetails WHERE prod_barCode = ? AND size_Id = ?";
            try (PreparedStatement getCurrentStock = connection.prepareStatement(getCurrentStockQuery)) {
                getCurrentStock.setString(1, currentBarCode);
                getCurrentStock.setInt(2, currentSizeId);

                ResultSet rs = getCurrentStock.executeQuery();
                if (rs.next()) {
                    currentStock = rs.getInt("stock_quantity");
                    System.out.println("curStock for " + currentBarCode + " (Size " + currentSizeId + "): " + currentStock);
                } else {
                    System.out.println("No stock found for this product and size.");
                    continue; // Skip this item if no stock is found
                }
            }

            // Calculate new stock
            newStock = currentStock - quantitySold;
            System.out.println("newStock for " + currentBarCode + " (Size " + currentSizeId + "): " + newStock);

            // Update stock in the database
            String updateQuery = "UPDATE warehousedetails SET stock_quantity = ? WHERE prod_barCode = ? AND size_Id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, newStock);
                preparedStatement.setString(2, currentBarCode);
                preparedStatement.setInt(3, currentSizeId);
                preparedStatement.executeUpdate();
            }
        }

        // Close the connection after updating all products
        connection.close();
    }

    int idForPDF = 0;
    // Cập nhật tổng số tiền và thời gian vào bảng receipt
    private void updateReceiptTotalAmount() throws SQLException {
        double totalAmount = Double.parseDouble(totalPrice.getText().replace("$", ""));

        // Lấy thời gian hiện tại và định dạng nó
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        String query = "INSERT INTO receipt (rec_date, rec_totalAmount) VALUES (?, ?)";

        try (Connection conn = connectionClass.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Thiết lập giá trị cho các tham số truy vấn
            stmt.setString(1, formattedDateTime); // Chèn ngày giờ
            stmt.setDouble(2, totalAmount);       // Chèn tổng số tiền
            stmt.executeUpdate();  // Thực thi câu lệnh SQL để chèn vào bảng receipt
            ResultSet generatedKeys = stmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                idForPDF = generatedKeys.getInt(1);
            }
            // In thông báo thành công nếu cần
            System.out.println("Receipt updated successfully with date and total amount.");

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("There was an error updating the receipt.");
            alert.showAndWait();
        }
    }

    // Hàm để lấy rec_Id vừa được thêm vào bảng receipt
    private int getLastInsertedRecId() {
        int lastRecId = -1; // Biến để lưu rec_Id vừa được thêm
        String query = "SELECT MAX(rec_Id) AS lastRecId FROM receipt"; // Lệnh SQL để lấy rec_Id lớn nhất

        try (Connection conn = connectionClass.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                lastRecId = rs.getInt("lastRecId"); // Lấy rec_Id lớn nhất
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("There was an error retrieving the last receipt ID.");
            alert.showAndWait();
        }
        return lastRecId; // Trả về rec_Id vừa được thêm
    }

    // Hàm để lấy số lượng sản phẩm từ cột quantity của tableView
    private int getSelectedProductQuantity() {
        if (menu_tableView.getSelectionModel().getSelectedItem() != null) {
            ProductData selectedProduct = menu_tableView.getSelectionModel().getSelectedItem();
            System.out.println("Selected product: " + selectedProduct.getProd_name() + ", Quantity: " + selectedProduct.getProd_quantity());
            return selectedProduct.getProd_quantity();
        } else {
            System.out.println("No item selected or table is empty.");
            return 0; // Trả về 0 nếu không có mục nào được chọn
        }
    }

    public void checkPhoneNumber(ActionEvent event) {
        String phoneNumber = textfieldphoneNumber.getText().trim();

        // Check if the phone number field is empty
        if (phoneNumber.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a phone number.");
            alert.showAndWait();
            return;
        }

        try {
            // Connect to the database
            Connection connection =connectionClass.getConnection(); // Use your database connection class
            String query = "SELECT * FROM customer WHERE cus_phoneNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, phoneNumber);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Phone number exists
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Notification");
                alert.setHeaderText(null);
                alert.setContentText("The phone number already exists in the system.");
                alert.showAndWait();
            } else {
                // Phone number does not exist
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Notification");
                alert.setHeaderText(null);
                alert.setContentText("The phone number does not exist in the system.");
                alert.showAndWait();
            }

            // Close the connection
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while checking the phone number.");
            alert.showAndWait();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleDashboardPage(java.awt.event.ActionEvent event) {
    }

    @FXML
    void handleProductPage(java.awt.event.ActionEvent event) {
    }

    private String selectedCategory = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            categoryComboBox.getItems().addAll("Men", "Women", "Kids");
            brandListCoboBox();

            categoryComboBox.valueProperty().addListener((observable, oldvalue, newvalue) -> {
                selectedCategory = newvalue;
                menuDisplayCard(null);
            });

            //search
            textfield_search.textProperty().addListener((observable, oldvalue, newvalue) -> {
                System.out.println("Text changed");
                menuDisplayCard(null);
            });

            System.out.println("menu_gridPane is: " + menu_gridPane);
            if (menu_gridPane == null) {
                System.err.println("menu_gridPane is not initialized! Check FXML setup.");
            } else {
                menuDisplayCard(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtBarcode.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    searchProduct(txtBarcode.getText());
                    updateTotalPrice();
                    txtBarcode.clear();
                }
            }
        });

        try {
            brandListComboBox();  // Ensure this populates the ComboBox with brands
            categoryListComboBox();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add an event listener for brandComboBox
        brandComboBox.setOnAction(event -> menuDisplayCard(brandComboBox.getValue()));
        categoryComboBox.valueProperty().addListener((observale, oldvalue, nevalue) -> {
            System.out.println("Category switched");
        });

        colProductName.setCellValueFactory(new PropertyValueFactory<>("prod_name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("prod_quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("prod_price"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("prod_size"));

        menu_tableView.setItems(productList);  // Assuming productList is populated

        try {

            brandListComboBox();
            categoryListComboBox();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        textfieldphoneNumber.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    String text = textfieldphoneNumber.getText();
                    System.out.println(text);
                    textfieldphoneNumber.setText("");
                }
            }
        });
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

























