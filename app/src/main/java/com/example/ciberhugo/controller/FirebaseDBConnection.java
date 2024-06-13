/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.controller;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para manejar la conexión y operaciones con Firebase Firestore.
 */
public class FirebaseDBConnection {
    private final FirebaseFirestore db;
    private final CollectionReference usersCollection;
    private final CollectionReference logsCollection;

    /**
     * Constructor que inicializa la conexión con Firestore y las referencias a las colecciones.
     */
    public FirebaseDBConnection() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
        logsCollection = db.collection("logs");
    }

    /**
     * Método para insertar un nuevo usuario en la colección "users" de Firestore.
     * @param email Correo electrónico del usuario
     * @param username Nombre de usuario
     * @param password Contraseña (hash)
     * @param phone Número de teléfono del usuario
     * @param admin Indicador de si el usuario es administrador
     * @param banned Indicador de si el usuario está baneado
     * @param timeLeft Tiempo restante del usuario (en horas)
     */
    public void insertUser(String email, String username, String password, String phone,
                           boolean admin, boolean banned, int timeLeft) {
        // Crear un mapa con los datos del usuario
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("user", username);
        user.put("password", password);
        user.put("phone", phone);
        user.put("admin", admin);
        user.put("banned", banned);
        user.put("timeLeft", timeLeft);
        user.put("Order", new ArrayList<>()); // Ejemplo de campo adicional

        // Agregar el usuario a la colección "users" en Firestore
        usersCollection.add(user).addOnSuccessListener(documentReference -> {
            System.out.println("User added with ID: " + documentReference.getId());
        }).addOnFailureListener(e -> {
            System.err.println("Error adding user: " + e.getMessage());
        });
    }

    /**
     * Método para insertar un nuevo registro de log en la colección "logs" de Firestore.
     * @param email Correo electrónico asociado al log
     * @param action Acción realizada (ej. "Login", "Logout")
     * @param detail Detalle adicional del log
     */
    public void insertLog(String email, String action, String detail) {
        // Crear un mapa con los datos del log
        Map<String, Object> log = new HashMap<>();
        log.put("email", email);
        log.put("action", action);
        log.put("date", new Date()); // Fecha actual del log
        log.put("detail", detail);

        // Agregar el log a la colección "logs" en Firestore
        logsCollection.add(log).addOnSuccessListener(documentReference -> {
            System.out.println("Log added with ID: " + documentReference.getId());
        }).addOnFailureListener(e -> {
            System.err.println("Error adding log: " + e.getMessage());
        });
    }
}
