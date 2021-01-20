package com.melnele.trips.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public
class LatLong implements Serializable {
    public double latitude;
    public double longitude;

    public LatLong() {
    }

    public LatLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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

    @NotNull
    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}