/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.model;

/**
 * Modelo de datos para una reserva.
 */
public class Reservation {
    private String id;       // Identificador único de la reserva
    private String email;    // Correo electrónico del usuario que realizó la reserva
    private String reason;   // Motivo de la reserva
    private String date;     // Fecha de la reserva
    private String time;     // Hora de la reserva
    private String people;   // Número de personas para la reserva

    /**
     * Constructor de la clase Reservation.
     * @param id Identificador único de la reserva
     * @param email Correo electrónico del usuario que realizó la reserva
     * @param reason Motivo de la reserva
     * @param date Fecha de la reserva
     * @param time Hora de la reserva
     * @param people Número de personas para la reserva
     */
    public Reservation(String id, String email, String reason, String date, String time, String people) {
        this.id = id;
        this.email = email;
        this.reason = reason;
        this.date = date;
        this.time = time;
        this.people = people;
    }

    /**
     * Método getter para obtener el identificador único de la reserva.
     * @return Identificador único de la reserva
     */
    public String getId() {
        return id;
    }

    /**
     * Método setter para establecer el identificador único de la reserva.
     * @param id Identificador único de la reserva
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Método getter para obtener el correo electrónico del usuario que realizó la reserva.
     * @return Correo electrónico del usuario
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método setter para establecer el correo electrónico del usuario que realizó la reserva.
     * @param email Correo electrónico del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Método getter para obtener el motivo de la reserva.
     * @return Motivo de la reserva
     */
    public String getReason() {
        return reason;
    }

    /**
     * Método setter para establecer el motivo de la reserva.
     * @param reason Motivo de la reserva
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Método getter para obtener la fecha de la reserva.
     * @return Fecha de la reserva
     */
    public String getDate() {
        return date;
    }

    /**
     * Método setter para establecer la fecha de la reserva.
     * @param date Fecha de la reserva
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Método getter para obtener la hora de la reserva.
     * @return Hora de la reserva
     */
    public String getTime() {
        return time;
    }

    /**
     * Método setter para establecer la hora de la reserva.
     * @param time Hora de la reserva
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Método getter para obtener el número de personas para la reserva.
     * @return Número de personas para la reserva
     */
    public String getPeople() {
        return people;
    }

    /**
     * Método setter para establecer el número de personas para la reserva.
     * @param people Número de personas para la reserva
     */
    public void setPeople(String people) {
        this.people = people;
    }

    /**
     * Método para obtener la fecha de la reserva formateada.
     * Este método podría ser modificado para aplicar cualquier formateo adicional necesario.
     * @return Fecha de la reserva
     */
    public String getFormattedDate() {
        return date;
    }
}
