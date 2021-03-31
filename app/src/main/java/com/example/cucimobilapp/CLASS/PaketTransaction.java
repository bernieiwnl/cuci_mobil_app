package com.example.cucimobilapp.CLASS;

import java.util.Date;
import java.util.List;

public class PaketTransaction {

    private String id;
    private String package_name;
    private Integer package_price;
    private String package_information;
    private String package_vehicle_type;
    private List<String> package_photo;
    private Date transaction_date;
    private String transaction_customer_name;
    private String customer_id;
    private String package_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public Integer getPackage_price() {
        return package_price;
    }

    public void setPackage_price(Integer package_price) {
        this.package_price = package_price;
    }

    public String getPackage_information() {
        return package_information;
    }

    public void setPackage_information(String package_information) {
        this.package_information = package_information;
    }

    public String getPackage_vehicle_type() {
        return package_vehicle_type;
    }

    public void setPackage_vehicle_type(String package_vehicle_type) {
        this.package_vehicle_type = package_vehicle_type;
    }

    public List<String> getPackage_photo() {
        return package_photo;
    }

    public void setPackage_photo(List<String> package_photo) {
        this.package_photo = package_photo;
    }

    public Date getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(Date transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getTransaction_customer_name() {
        return transaction_customer_name;
    }

    public void setTransaction_customer_name(String transaction_customer_name) {
        this.transaction_customer_name = transaction_customer_name;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public PaketTransaction(){

    }



    public PaketTransaction(String id, String nama, String info, Integer price, List<String> foto, String type, Date transaction_date, String transaction_customer_name, String customer_id, String package_id){
        this.setPackage_name(nama);
        this.setPackage_information(info);
        this.setPackage_price(price);
        this.setPackage_photo(foto);
        this.setPackage_vehicle_type(type);
        this.setId(id);
        this.setTransaction_date(transaction_date);
        this.setTransaction_customer_name(transaction_customer_name);
        this.setCustomer_id(customer_id);
        this.setPackage_id(package_id);
    }



}
