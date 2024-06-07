package com.example.ciberhugo.controller;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDBConnection {
    private final FirebaseFirestore db;
    private final CollectionReference usersCollection;
    private final CollectionReference logsCollection;

    public FirebaseDBConnection() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
        logsCollection = db.collection("logs");
    }

    public void insertUser(String email, String username, String password, String phone, boolean admin, boolean banned, int timeLeft) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("user", username);
        user.put("password", password);
        user.put("phone", phone);
        user.put("admin", admin);
        user.put("banned", banned);
        user.put("timeLeft", timeLeft);
        user.put("Order", new ArrayList<>());

        usersCollection.add(user).addOnSuccessListener(documentReference -> {
            System.out.println("User added with ID: " + documentReference.getId());
        }).addOnFailureListener(e -> {
            System.err.println("Error adding user: " + e.getMessage());
        });
    }

    public void insertLog(String email, String action, String detail) {
        Map<String, Object> log = new HashMap<>();
        log.put("email", email);
        log.put("action", action);
        log.put("date", new Date());
        log.put("detail", detail);

        logsCollection.add(log).addOnSuccessListener(documentReference -> {
            System.out.println("Log added with ID: " + documentReference.getId());
        }).addOnFailureListener(e -> {
            System.err.println("Error adding log: " + e.getMessage());
        });
    }
}
