package com.example.cucimobilapp.CLASS;

public class Customer {

    private String customer_name;
    private String customer_email;
    private String customer_address;
    private String customer_phone_number;
    private String customer_profile_picture;
    private String customer_vehicle_number;
    private String customer_vehicle_type;
    private String id;


    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_phone_number() {
        return customer_phone_number;
    }

    public void setCustomer_phone_number(String customer_phone_number) {
        this.customer_phone_number = customer_phone_number;
    }

    public String getCustomer_profile_picture() {
        return customer_profile_picture;
    }

    public void setCustomer_profile_picture(String customer_profile_picture) {
        this.customer_profile_picture = customer_profile_picture;
    }

    public String getCustomer_vehicle_number() {
        return customer_vehicle_number;
    }

    public void setCustomer_vehicle_number(String customer_vehicle_number) {
        this.customer_vehicle_number = customer_vehicle_number;
    }

    public String getCustomer_vehicle_type() {
        return customer_vehicle_type;
    }

    public void setCustomer_vehicle_type(String customer_vehicle_type) {
        this.customer_vehicle_type = customer_vehicle_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer(){

    }

    public Customer(String customer_name, String customer_address, String customer_email, String customer_phone_number, String customer_profile_picture, String customer_vehicle_number, String customer_vehicle_type){
        this.setCustomer_name(customer_name);
        this.setCustomer_address(customer_address);
        this.setCustomer_email(customer_email);
        this.setCustomer_phone_number(customer_phone_number);
        this.setCustomer_profile_picture(customer_profile_picture);
        this.setCustomer_vehicle_number(customer_vehicle_number);
        this.setCustomer_vehicle_type(customer_vehicle_type);
    }
}
