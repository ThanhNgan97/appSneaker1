package com.example.sneakershop3.Models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class productDetails {
    private SimpleIntegerProperty productID;
    private SimpleStringProperty productCode;
    private SimpleStringProperty productName;
    private SimpleStringProperty unit;
    private SimpleIntegerProperty quantity;
    private SimpleDoubleProperty price;
    private SimpleDoubleProperty totalPrice;

    public productDetails() {
    }

    public productDetails(SimpleIntegerProperty productID, SimpleStringProperty productCode, SimpleStringProperty productName, SimpleStringProperty unit, SimpleIntegerProperty quantity, SimpleDoubleProperty price, SimpleDoubleProperty totalPrice) {
        this.productID = productID;
        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
    }
    public int getProductID() {
        return productID.get();
    }

    public SimpleIntegerProperty productIDProperty() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID.set(productID);
    }

    public String getProductCode() {
        return productCode.get();
    }

    public SimpleStringProperty productCodeProperty() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode.set(productCode);
    }

    public String getProductName() {
        return productName.get();
    }

    public SimpleStringProperty productNameProperty() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public String getUnit() {
        return unit.get();
    }

    public SimpleStringProperty unitProperty() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public double getTotalPrice() {
        return totalPrice.get();
    }

    public SimpleDoubleProperty totalPriceProperty() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice.set(totalPrice);
    }
}
