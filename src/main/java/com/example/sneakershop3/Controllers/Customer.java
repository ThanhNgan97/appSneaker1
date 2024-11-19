package com.example.sneakershop3.Controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Customer {
    private String cus_phoneNumber;
    private String cus_name;

    public Customer(String cus_phoneNumber, String cus_name) {
        this.cus_phoneNumber = cus_phoneNumber;
        this.cus_name = cus_name;
    }

    public String getCus_phoneNumber() {
        return cus_phoneNumber;
    }

    public String getCus_name() {
        return cus_name;
    }
}