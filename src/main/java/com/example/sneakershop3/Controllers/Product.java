package com.example.sneakershop3.Controllers;

public class Product {
    private String productCode;
    private String productName;
    private String brand;
    private String category;
    private int size;
    private String unit;
    private double price;

    // Constructor, getters, and setters
    public Product(String productCode, String productName, String brand, String category, int size, String unit, double price) {
        this.productCode = productCode;
        this.productName = productName;
        this.brand = brand;
        this.category = category;
        this.size = size;
        this.unit = unit;
        this.price = price;
    }
}


