package com.example.sneakershop3.Models;

public class TopSale {
    private String productName;
    private int totalSale;

    public TopSale(String productName, int totalSale) {
        this.productName = productName;
        this.totalSale = totalSale;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(int totalSale) {
        this.totalSale = totalSale;
    }
}

