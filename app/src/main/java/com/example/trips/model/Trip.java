package com.example.trips.model;

import java.io.Serializable;
import java.util.Date;

public class Trip implements Serializable {
    private String id;
    private String name;
    private Address startPoint;
    private Address endPoint;
    private Date time;
    private TripStatus status = TripStatus.UPCOMING;
    private int repetition = 0; //Bonus
    private String[] notes;
    private Boolean roundTrip = false; //One Direction: false or Round Trip: true

    public Trip() {
    }

    public Trip(String id, String name, Address startPoint, Address endPoint, Date time) {
        this.id = id;
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

    public Address getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Address startPoint) {
        this.startPoint = startPoint;
    }

    public Address getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Address endPoint) {
        this.endPoint = endPoint;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
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
