package com.example.sneakershop3.Report;

public class billData {
    private String productName;
    private int quantity;
    private double price;
    private double total;

    private int billId;

    public billData(String productName, int quantity, double price, double total, int billId) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.billId = billId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }
}
