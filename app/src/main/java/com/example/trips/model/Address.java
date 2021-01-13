package com.example.trips.model;

import java.io.Serializable;

public class Address implements Serializable {
    private String id;
    private String name;
    private LatLong latLong;
    private String address;

    public Address() {
    }

    public Address(String id, String name, LatLong latLong, String address) {
        this.id = id;
        this.name = name;
        this.latLong = latLong;
        this.address = address;
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

    public LatLong getLatLong() {
        return latLong;
    }

    public void setLatLong(LatLong latLong) {
        this.latLong = latLong;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}