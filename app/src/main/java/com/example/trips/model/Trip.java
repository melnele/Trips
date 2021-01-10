package com.example.trips.model;

import com.google.android.libraries.places.api.model.Place;

import java.util.Date;

public class Trip {
    private String name;
    private Place startPoint;
    private Place endPoint;
    private Date time;
    private int status = 0; // Upcoming = 0, Done = 1, Cancelled = 2
    private int repetition = 0; //Bonus
    private String[] notes;
    private Boolean roundTrip = false; //One Direction: false or Round Trip: true

    public Trip(String name, Place startPoint, Place endPoint, Date time) {
        this.name = name;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Place getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Place startPoint) {
        this.startPoint = startPoint;
    }

    public Place getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Place endPoint) {
        this.endPoint = endPoint;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public String[] getNotes() {
        return notes;
    }

    public void setNotes(String[] notes) {
        this.notes = notes;
    }

    public Boolean getRoundTrip() {
        return roundTrip;
    }

    public void setRoundTrip(Boolean roundTrip) {
        this.roundTrip = roundTrip;
    }
}
