package com.example.cucimobilapp.CLASS;

import java.io.Serializable;
import java.util.List;

public class Paket implements Serializable {

    private String id;
    private String package_name;
    private Integer package_price;
    private String package_information;
    private String package_vehicle_type;
    private List<String> package_photo;

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

    public List<String> getPackage_photo() {
        return package_photo;
    }

    public void setPackage_photo(List<String> package_photo) {
        this.package_photo = package_photo;
    }

    public String getPackage_vehicle_type() {
        return package_vehicle_type;
    }

    public void setPackage_vehicle_type(String package_vehicle_type) {
        this.package_vehicle_type = package_vehicle_type;
    }

    public Paket(){

    }

    public Paket(String id, String nama, String info, Integer price, List<String> foto, String type){
        this.setPackage_name(nama);
        this.setPackage_information(info);
        this.setPackage_price(price);
        this.setPackage_photo(foto);
        this.setPackage_vehicle_type(type);
        this.setId(id);

    }


}
