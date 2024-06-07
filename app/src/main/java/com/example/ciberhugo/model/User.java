package com.example.ciberhugo.model;

public class User {
    private String email;
    private String username;
    private String phone;
    private String profileUrl;
    private boolean isAdmin;
    private long timeLeft;

    public User(String email, String username, String phone, boolean isAdmin, long timeLeft) {
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.isAdmin = isAdmin;
        this.timeLeft = timeLeft;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
