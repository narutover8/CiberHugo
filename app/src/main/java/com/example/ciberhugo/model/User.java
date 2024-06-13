/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.model;

/**
 * Modelo de datos para un usuario.
 */
public class User {
    private String id;           // Identificador único del usuario
    private String email;        // Correo electrónico del usuario
    private String username;     // Nombre de usuario
    private String phone;        // Número de teléfono del usuario
    private boolean isAdmin;     // Indica si el usuario es administrador
    private long timeLeft;       // Tiempo restante para alguna función específica (ej. tiempo de sesión)

    /**
     * Constructor de la clase User.
     * @param id Identificador único del usuario
     * @param email Correo electrónico del usuario
     * @param username Nombre de usuario
     * @param phone Número de teléfono del usuario
     * @param isAdmin Indica si el usuario es administrador
     * @param timeLeft Tiempo restante para alguna función específica
     */
    public User(String id, String email, String username, String phone, boolean isAdmin, long timeLeft) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.isAdmin = isAdmin;
        this.timeLeft = timeLeft;
    }

    /**
     * Método getter para obtener el identificador único del usuario.
     * @return Identificador único del usuario
     */
    public String getId() {
        return id;
    }

    /**
     * Método setter para establecer el identificador único del usuario.
     * @param id Identificador único del usuario
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Método getter para obtener el correo electrónico del usuario.
     * @return Correo electrónico del usuario
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método setter para establecer el correo electrónico del usuario.
     * @param email Correo electrónico del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Método getter para obtener el nombre de usuario.
     * @return Nombre de usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Método setter para establecer el nombre de usuario.
     * @param username Nombre de usuario
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Método getter para obtener el número de teléfono del usuario.
     * @return Número de teléfono del usuario
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Método setter para establecer el número de teléfono del usuario.
     * @param phone Número de teléfono del usuario
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Método getter para verificar si el usuario es administrador.
     * @return true si el usuario es administrador, false de lo contrario
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Método setter para establecer si el usuario es administrador.
     * @param admin true si el usuario es administrador, false de lo contrario
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Método getter para obtener el tiempo restante para alguna función específica.
     * @return Tiempo restante
     */
    public long getTimeLeft() {
        return timeLeft;
    }

    /**
     * Método setter para establecer el tiempo restante para alguna función específica.
     * @param timeLeft Tiempo restante
     */
    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }
}
