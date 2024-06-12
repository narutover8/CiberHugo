package com.example.ciberhugo.model;

public class User {
    private String id;
    private String email;
    private String username;
    private String phone;
    private boolean isAdmin;
    private long timeLeft;

    public User(String id, String email, String username, String phone, boolean isAdmin, long timeLeft) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.isAdmin = isAdmin;
        this.timeLeft = timeLeft;
    }

    // Getters and setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
    public long getTimeLeft() { return timeLeft; }
    public void setTimeLeft(long timeLeft) { this.timeLeft = timeLeft; }
}
