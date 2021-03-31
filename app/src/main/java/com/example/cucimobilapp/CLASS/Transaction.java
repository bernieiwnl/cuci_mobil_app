package com.example.cucimobilapp.CLASS;

import java.util.Date;
import java.util.List;

public class Transaction {

    private String transaction_id;
    private String transaction_customer_id;
    private String transaction_customer_type;
    private String transaction_customer_name;
    private String transaction_customer_address;
    private String transaction_customer_phone_number;
    private String transaction_customer_vehicle_number;
    private Date transaction_date;
    private Integer transaction_price;
    private List<String> transaction_photo;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTransaction_customer_id() {
        return transaction_customer_id;
    }

    public void setTransaction_customer_id(String transaction_customer_id) {
        this.transaction_customer_id = transaction_customer_id;
    }

    public String getTransaction_customer_type() {
        return transaction_customer_type;
    }

    public void setTransaction_customer_type(String transaction_customer_type) {
        this.transaction_customer_type = transaction_customer_type;
    }

    public String getTransaction_customer_name() {
        return transaction_customer_name;
    }

    public void setTransaction_customer_name(String transaction_customer_name) {
        this.transaction_customer_name = transaction_customer_name;
    }

    public String getTransaction_customer_address() {
        return transaction_customer_address;
    }

    public void setTransaction_customer_address(String transaction_customer_address) {
        this.transaction_customer_address = transaction_customer_address;
    }

    public String getTransaction_customer_phone_number() {
        return transaction_customer_phone_number;
    }

    public void setTransaction_customer_phone_number(String transaction_customer_phone_number) {
        this.transaction_customer_phone_number = transaction_customer_phone_number;
    }

    public Date getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(Date transaction_date) {
        this.transaction_date = transaction_date;
    }

    public Integer getTransaction_price() {
        return transaction_price;
    }

    public void setTransaction_price(Integer transaction_price) {
        this.transaction_price = transaction_price;
    }

    public String getTransaction_customer_vehicle_number() {
        return transaction_customer_vehicle_number;
    }

    public void setTransaction_customer_vehicle_number(String transaction_customer_vehicle_number) {
        this.transaction_customer_vehicle_number = transaction_customer_vehicle_number;
    }

    public List<String> getTransaction_photo() {
        return transaction_photo;
    }

    public void setTransaction_photo(List<String> transaction_photo) {
        this.transaction_photo = transaction_photo;
    }
}
