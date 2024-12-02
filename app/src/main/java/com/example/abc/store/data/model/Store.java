package com.example.abc.store.data.model;

import com.example.abc.drink.data.model.RealIngredient;

import java.io.Serializable;
import java.util.List;

public class Store implements Serializable {
    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String coverImage;
    private double latitude;
    private double longitude;

    private List<RealIngredient> storage;

    public Store(){
    }

    public Store(String id, String name, String address, String phoneNumber, String coverImage, double latitude, double longitude, List<RealIngredient> storage) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.coverImage = coverImage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.storage = storage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<RealIngredient> getStorage() {
        return storage;
    }

    public void setStorage(List<RealIngredient> storage) {
        this.storage = storage;
    }
}

