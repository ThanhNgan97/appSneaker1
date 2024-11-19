package com.example.sneakershop3.Models;//package com.example.sneakershop3.Models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ProductData {
    private String prod_barCode;
    private SimpleStringProperty prod_name;
    private String prod_unit;
    private SimpleDoubleProperty prod_price;
    private String prod_image;
    private Integer cate_Id;
    private String prod_brand;
    private SimpleIntegerProperty size_name;
    private SimpleIntegerProperty recd_quantity;
    private String category; // Field for category
    private int quantity;

    public ProductData(SimpleStringProperty prod_name, SimpleIntegerProperty recd_quantity, SimpleDoubleProperty prod_price, SimpleIntegerProperty size_name) {
        this.prod_name = prod_name;
        this.recd_quantity = recd_quantity;
        this.prod_price = prod_price;
        this.size_name = size_name;
    }

    // Constructor 1
    public ProductData(String prod_barCode, String prod_name, String prod_unit, double prod_price, String prod_image, Integer cate_Id, String prod_brand) {
        this.prod_barCode = prod_barCode;
        this.prod_name = new SimpleStringProperty(prod_name);
        this.prod_unit = prod_unit;
        this.prod_price = new SimpleDoubleProperty(prod_price);
        this.prod_image = prod_image;
        this.cate_Id = cate_Id;
        this.prod_brand = prod_brand;
        this.size_name = new SimpleIntegerProperty(0); // Default initialization
        this.recd_quantity = new SimpleIntegerProperty(0); // Default initialization
    }

    public ProductData(String prod_barCode, SimpleStringProperty prod_name, String prod_unit, SimpleDoubleProperty prod_price, SimpleIntegerProperty size_name, SimpleIntegerProperty recd_quantity) {
        this.prod_barCode = prod_barCode;
        this.prod_name = prod_name;
        this.prod_unit = prod_unit;
        this.prod_price = prod_price;
        this.size_name = size_name;
        this.recd_quantity = recd_quantity;
    }

    // Constructor 2
    public ProductData(String prod_name, Double prod_price, Integer size_name, Integer recd_quantity) {
        this.prod_name = new SimpleStringProperty(prod_name);
        this.prod_price = new SimpleDoubleProperty(prod_price);
        this.size_name = new SimpleIntegerProperty(size_name != null ? size_name : 0); // Handling null
        this.recd_quantity = new SimpleIntegerProperty(recd_quantity != null ? recd_quantity : 0); // Handling null
    }

    // Constructor 3
    public ProductData(String prod_barCode, String prod_name, String prod_unit, Double prod_price, String prod_image, String prod_brand, Integer size_name, int recd_quantity, Integer cate_Id) {
        this.prod_barCode = prod_barCode;
        this.prod_name = new SimpleStringProperty(prod_name);
        this.prod_unit = prod_unit;
        this.prod_price = new SimpleDoubleProperty(prod_price);
        this.prod_image = prod_image;
        this.cate_Id = cate_Id;
        this.prod_brand = prod_brand;
        this.size_name = new SimpleIntegerProperty(size_name != null ? size_name : 0); // Handling null
        this.recd_quantity = new SimpleIntegerProperty(0); // Handling null
    }

    // Constructor 4
    public ProductData(String prod_barCode, String prod_name, String prod_unit, double prod_price, String prod_image, Integer cate_Id, String prod_brand, int size_name) {
        this.prod_barCode = prod_barCode;
        this.prod_name = new SimpleStringProperty(prod_name);
        this.prod_unit = prod_unit;
        this.prod_price = new SimpleDoubleProperty(prod_price);
        this.prod_image = prod_image;
        this.cate_Id = cate_Id;
        this.prod_brand = prod_brand;
        this.size_name = new SimpleIntegerProperty(size_name);
        this.recd_quantity = new SimpleIntegerProperty(0); // Default initialization
    }

    public ProductData(String prodBarCode, String prodName, String prodUnit, double prodPrice, String prodImage, Integer size, int quantity) {
        this.prod_barCode = prodBarCode;
        this.prod_name = new SimpleStringProperty(prodName);
        this.prod_unit = prodUnit;
        this.prod_price = new SimpleDoubleProperty(prodPrice);
        this.prod_image = prodImage;
        this.size_name = new SimpleIntegerProperty(size);
        this.recd_quantity = new SimpleIntegerProperty(quantity);
    }

    public ProductData(String productCode, String productName, String unit, double price, int size, int quantity) {
        this.prod_barCode = productCode;
        this.prod_name = new SimpleStringProperty(productName);
        this.prod_unit = unit;
        this.prod_price = new SimpleDoubleProperty(price);
        this.size_name = new SimpleIntegerProperty(size);
        this.recd_quantity = new SimpleIntegerProperty(quantity);
    }


    public int getQuantity() {
        return quantity;
    }

    // Phương thức để đặt số lượng
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getters and setters
    public String getProd_barCode() {

        return prod_barCode;
    }

    public void setProd_barCode(String prod_barCode) {
        this.prod_barCode = prod_barCode;
    }

    public String getProd_name() {
        return prod_name.get();
    }

    public SimpleStringProperty prod_nameProperty() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name.set(prod_name);
    }

    public String getProd_unit() {
        return prod_unit;
    }

    public void setProd_unit(String prod_unit) {
        this.prod_unit = prod_unit;
    }

    public double getProd_price() {
        return prod_price.get();
    }

    public SimpleDoubleProperty prod_priceProperty() {
        return prod_price;
    }

    public void setProd_price(double prod_price) {
        this.prod_price.set(prod_price);
    }

    public String getProd_image() {
        return prod_image;
    }

    public void setProd_image(String prod_image) {
        this.prod_image = prod_image;
    }

    public Integer getCate_Id() {
        return cate_Id;
    }

    public void setCate_Id(Integer cate_Id) {
        this.cate_Id = cate_Id;
    }

    public String getProd_brand() {
        return prod_brand;
    }

    public void setProd_brand(String prod_brand) {
        this.prod_brand = prod_brand;
    }

    public Integer getProd_size() {
        return size_name.get();
    }

    public SimpleIntegerProperty prod_sizeProperty() {
        return size_name;
    }

    public void setProd_size(Integer prod_size) {
        this.size_name.set(prod_size);
    }

    public int getProd_quantity() {
        return recd_quantity.get();
    }

    public SimpleIntegerProperty prod_quantityProperty() {
        return recd_quantity;
    }

    public void setProd_quantity(int prod_quantity) {
        this.recd_quantity.set(prod_quantity);
    }

    public String getCategory() {
        return category; // Getter for category
    }

    public void setCategory(String category) {
        this.category = category; // Setter for category
    }
}


