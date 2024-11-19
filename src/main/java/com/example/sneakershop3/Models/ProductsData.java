package com.example.sneakershop3.Models;

import com.example.sneakershop3.dbConnection.connectionClass;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductsData {
    private final SimpleStringProperty prod_barCode;
    private final SimpleStringProperty prod_name;
    private final SimpleStringProperty prod_unit;
    private final SimpleDoubleProperty prod_price;
    private final SimpleStringProperty prod_image;
    private final SimpleIntegerProperty cate_Id;
    private final SimpleStringProperty prod_brand;
    private final SimpleIntegerProperty size_Id;
    private final SimpleIntegerProperty size_name;
    private final SimpleIntegerProperty ware_stock;
    private final SimpleStringProperty ware_lastUpdate;
    private final SimpleStringProperty categoryName;

    // Constructor
    public ProductsData(String barcode, String name, String unit, double price, String imagePath, int categoryId, String brand, int sizeId, int sizeName, int stock, String lastUpdate) {
        this.prod_barCode = new SimpleStringProperty(barcode);
        this.prod_name = new SimpleStringProperty(name);
        this.prod_unit = new SimpleStringProperty(unit);
        this.prod_price = new SimpleDoubleProperty(price);
        this.prod_image = new SimpleStringProperty(imagePath);
        this.cate_Id = new SimpleIntegerProperty(categoryId);
        this.prod_brand = new SimpleStringProperty(brand);
        this.size_Id = new SimpleIntegerProperty(sizeId);
        this.size_name = new SimpleIntegerProperty(sizeName);
        this.ware_stock = new SimpleIntegerProperty(stock);
        this.ware_lastUpdate = new SimpleStringProperty(lastUpdate);
        this.categoryName = new SimpleStringProperty(getCategoryNameFromId(categoryId));
    }

    private String getCategoryNameFromId(int cateId) {
        return switch (cateId) {
            case 1 -> "Men";
            case 2 -> "Women";
            case 3 -> "Kids";
            default -> "Unknown";
        };
    }

    public String getCategoryName() {
        return categoryName.get();
    }

    // Getters
    public String getProd_barCode() {
        return prod_barCode.get();
    }

    public String getProd_name() {
        return prod_name.get();
    }

    public String getProd_unit() {
        return prod_unit.get();
    }

    public double getProd_price() {
        return prod_price.get();
    }

    public String getProd_image() {
        return prod_image.get();
    }

    public int getCate_Id() {
        return cate_Id.get();
    }

    public String getProd_brand() {
        return prod_brand.get();
    }

    public int getSize_Id() {
        return size_Id.get();
    }

    public int getSize_name() {
        return size_name.get();
    }

    public int getWare_stock() {
        return ware_stock.get();
    }

    public String getWare_lastUpdate() {
        return ware_lastUpdate.get();
    }

    // Setters
    public void setProd_barCode(String value) {
        prod_barCode.set(value);
    }

    public void setProd_name(String value) {
        prod_name.set(value);
    }

    public void setProd_unit(String value) {
        prod_unit.set(value);
    }

    public void setProd_price(double value) {
        prod_price.set(value);
    }

    public void setProd_image(String value) {
        prod_image.set(value);
    }

    public void setCate_Id(int value) {
        cate_Id.set(value);
        categoryName.set(getCategoryNameFromId(value)); // Cập nhật categoryName
    }

    public void setProd_brand(String value) {
        prod_brand.set(value);
    }

    public void setSize_Id(int value) {
        size_Id.set(value);
    }

    public void setSize_name(int value) {
        size_name.set(value);
    }

    public void setWare_stock(int value) {
        ware_stock.set(value);
    }

    public void setWare_lastUpdate(String value) {
        ware_lastUpdate.set(value);
    }

    // Thêm sản phẩm vào cơ sở dữ liệu
    public boolean addProductToDatabase(String imagePath) {
        System.out.println(imagePath);
        try (Connection connection = connectionClass.getConnection()) {
            if (connection == null) {
                System.out.println("Không thể kết nối với cơ sở dữ liệu.");
                return false;
            }

            // Kiểm tra xem mã barcode đã tồn tại trong bảng productDetails chưa
            String sqlCheckBarcode = "SELECT COUNT(*) FROM productDetails WHERE prod_barCode = ?";
            boolean isBarcodeExists = false;
            try (PreparedStatement checkStatement = connection.prepareStatement(sqlCheckBarcode)) {
                checkStatement.setString(1, getProd_barCode());
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        isBarcodeExists = resultSet.getInt(1) > 0;
                    }
                }
            }

            // Nếu barcode chưa tồn tại, thêm sản phẩm vào bảng productDetails
            if (!isBarcodeExists) {
                String sqlProductDetails = "INSERT INTO productDetails (prod_barCode, prod_name, prod_unit, prod_price, prod_image, cate_Id, prod_brand) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sqlProductDetails)) {
                    statement.setString(1, getProd_barCode());
                    statement.setString(2, getProd_name());
                    statement.setString(3, getProd_unit());
                    statement.setDouble(4, getProd_price());
                    statement.setString(5, imagePath);
                    statement.setInt(6, getCate_Id());
                    statement.setString(7, getProd_brand());
                    statement.executeUpdate();
                }
            } else {
                System.out.println("Sản phẩm với mã barcode đã tồn tại, chỉ thêm vào warehouseDetails.");
            }

            // Lấy thời gian hiện tại
            String currentTime = java.time.LocalDateTime.now().toString();

            // Kiểm tra tồn tại của prod_barCode và size_Id trong bảng warehouseDetails
            String sqlCheckWarehouseDetails = "SELECT COUNT(*) FROM warehouseDetails WHERE prod_barCode = ? AND size_Id = ?";
            boolean isSizeExists = false;
            try (PreparedStatement checkSizeStatement = connection.prepareStatement(sqlCheckWarehouseDetails)) {
                checkSizeStatement.setString(1, getProd_barCode());
                checkSizeStatement.setInt(2, getSize_Id());
                try (ResultSet resultSet = checkSizeStatement.executeQuery()) {
                    if (resultSet.next()) {
                        isSizeExists = resultSet.getInt(1) > 0;
                    }
                }
            }

            // Nếu không tồn tại cùng barcode và size_Id thì mới thêm vào warehouseDetails
            if (!isSizeExists) {
                String sqlWarehouseDetails = "INSERT INTO warehouseDetails (ware_Id, prod_barCode, size_Id, stock_quantity) VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE stock_quantity = ?";
                String sqlUpdateWarehouse = "UPDATE warehouse SET ware_lastUpdate = ? WHERE ware_Id = ?";
                try (PreparedStatement statement2 = connection.prepareStatement(sqlWarehouseDetails);
                     PreparedStatement statement3 = connection.prepareStatement(sqlUpdateWarehouse)) {
                    statement2.setInt(1, 1); // Giả sử ware_Id là 1
                    statement2.setString(2, getProd_barCode());
                    statement2.setInt(3, getSize_Id());
                    statement2.setInt(4, getWare_stock());
                    statement2.setInt(5, getWare_stock()); // Cập nhật stock_quantity nếu tồn tại
                    statement2.executeUpdate();

                    // Cập nhật thời gian
                    statement3.setString(1, currentTime);
                    statement3.setInt(2, 1);
                    statement3.executeUpdate();
                }
            } else {
                System.out.println("Barcode và Size đã tồn tại trong warehouseDetails, không thêm mới.");
                return false;
            }

            // Cập nhật thời gian cuối cùng trong đối tượng
            this.ware_lastUpdate.set(currentTime);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProductFromDatabase(int sizeId) {
        try (Connection connection = connectionClass.getConnection()) {
            if (connection == null) {
                System.out.println("Không thể kết nối với cơ sở dữ liệu.");
                return false;
            }

            // Xoá thông tin sản phẩm khỏi bảng warehouseDetails dựa trên prod_barCode và size_Id
            String sqlDeleteWarehouseDetails = "DELETE FROM warehousedetails WHERE prod_barCode = ? AND size_Id = ?";
            try (PreparedStatement statement1 = connection.prepareStatement(sqlDeleteWarehouseDetails)) {
                statement1.setString(1, getProd_barCode());
                statement1.setInt(2, sizeId); // Xóa theo sizeId cụ thể
                int rowsAffected = statement1.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.println("Không tìm thấy sản phẩm với mã barcode và sizeId tương ứng.");
                    return false;
                }
            }

            // Kiểm tra nếu prod_barCode còn tồn tại trong warehousedetails
            String sqlCheckRemainingSizes = "SELECT COUNT(*) FROM warehousedetails WHERE prod_barCode = ?";
            boolean hasRemainingSizes = false;
            try (PreparedStatement checkStatement = connection.prepareStatement(sqlCheckRemainingSizes)) {
                checkStatement.setString(1, getProd_barCode());
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        hasRemainingSizes = resultSet.getInt(1) > 0;
                    }
                }
            }

            // Nếu không còn size nào trong warehouseDetails, xóa sản phẩm khỏi productdetails
            if (!hasRemainingSizes) {
                String sqlDeleteProductDetails = "DELETE FROM productdetails WHERE prod_barCode = ?";
                try (PreparedStatement statement2 = connection.prepareStatement(sqlDeleteProductDetails)) {
                    statement2.setString(1, getProd_barCode());
                    statement2.executeUpdate();
                }
                System.out.println("Xóa thành công sản phẩm khỏi cả bảng productdetails và warehousedetails.");
            } else {
                System.out.println("Sản phẩm vẫn còn các size khác trong warehousedetails.");
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProductInDatabase(int newSizeId) {
        try (Connection connection = connectionClass.getConnection()) {
            if (connection == null) {
                System.out.println("Cannot connect to database.");
                return false;
            }

            // Kiểm tra nếu size_Id mới đã tồn tại với cùng prod_barCode, nhưng không kiểm tra nếu size_Id cũ bằng với size_Id mới
            String sqlCheckSizeId = "SELECT COUNT(*) FROM warehousedetails WHERE prod_barCode = ? AND size_Id = ? AND size_Id != ?";
            boolean isSizeIdExists = false;
            try (PreparedStatement checkStatement = connection.prepareStatement(sqlCheckSizeId)) {
                checkStatement.setString(1, getProd_barCode());
                checkStatement.setInt(2, newSizeId);
                checkStatement.setInt(3, getSize_Id()); // Bỏ qua kiểm tra nếu size_Id cũ giống size_Id mới
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        isSizeIdExists = resultSet.getInt(1) > 0;
                    }
                }
            }

            // Nếu size_Id mới đã tồn tại và không trùng với size_Id cũ, không cho phép cập nhật
            if (isSizeIdExists) {
                System.out.println("Size_Id mới đã tồn tại cho sản phẩm này, không thể cập nhật.");
                return false;
            }

            // Thực hiện cập nhật size_Id trong warehousedetails
            String sqlUpdateSizeId = "UPDATE warehousedetails SET size_Id = ?, stock_quantity = ? WHERE prod_barCode = ? AND size_Id = ?";
            try (PreparedStatement statement2 = connection.prepareStatement(sqlUpdateSizeId)) {
                statement2.setInt(1, newSizeId); // size_Id mới
                statement2.setInt(2, getWare_stock()); // stock_quantity
                statement2.setString(3, getProd_barCode()); // prod_barCode
                statement2.setInt(4, getSize_Id()); // size_Id cũ
                int rowsUpdated = statement2.executeUpdate();

                if (rowsUpdated == 0) {
                    System.out.println("Cập nhật thất bại, sản phẩm với size_Id cũ không tồn tại.");
                    return false;
                }
            }

            // Cập nhật thời gian cuối cùng trong bảng warehouse
            String currentTime = java.time.LocalDateTime.now().toString();
            String sqlUpdateWarehouseTime = "UPDATE warehouse SET ware_lastUpdate = ? WHERE ware_Id = ?";
            try (PreparedStatement statement3 = connection.prepareStatement(sqlUpdateWarehouseTime)) {
                statement3.setString(1, currentTime);
                statement3.setInt(2, 1); // Giả sử ware_Id = 1
                statement3.executeUpdate();
            }

            // Cập nhật trường ware_lastUpdate trong đối tượng
            setWare_lastUpdate(currentTime);

            System.out.println("Cập nhật size_Id thành công.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}