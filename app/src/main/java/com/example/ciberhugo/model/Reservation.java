package com.example.ciberhugo.model;

public class Reservation {
    private String id;
    private String email;
    private String reason;
    private String date;
    private String time;
    private String people;

    public Reservation(String id, String email, String reason, String date, String time, String people) {
        this.id = id;
        this.email = email;
        this.reason = reason;
        this.date = date;
        this.time = time;
        this.people = people;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getFormattedDate() {
        return date;
    }
}
