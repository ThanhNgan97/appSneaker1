package com.example.sneakershop3.Models;

import java.sql.Date;

public class CustomerData {
    private Integer id;
    private Integer customerID;
    private Double total;
    private Date date;
    private String emUsername;

    public CustomerData(Integer id, Integer customerID, Double total, Date date, String emUsername) {
        this.id = id;
        this.customerID = customerID;
        this.total = total;
        this.date = date;
        this.emUsername = emUsername;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmUsername() {
        return emUsername;
    }

    public void setEmUsername(String emUsername) {
        this.emUsername = emUsername;
    }
}
