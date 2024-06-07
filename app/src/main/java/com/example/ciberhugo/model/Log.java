package com.example.ciberhugo.model;

import java.util.Date;

public class Log {
    private String email;
    private String action;
    private String detail;
    private Date date;

    // Constructor, getters and setters

    public Log(String email, String action, String detail, Date date) {
        this.email = email;
        this.action = action;
        this.detail = detail;
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public String getAction() {
        return action;
    }

    public String getDetail() {
        return detail;
    }

    public Date getDate() {
        return date;
    }
}