/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.model;

import java.util.Date;

/**
 * Modelo de datos para un registro de actividad (log).
 */
public class Log {
    private String email;    // Correo electrónico asociado al registro
    private String action;   // Acción realizada (ej. inicio de sesión, modificación, etc.)
    private String detail;   // Detalles adicionales del registro
    private Date date;       // Fecha y hora del registro

    /**
     * Constructor de la clase Log.
     * @param email Correo electrónico asociado al registro
     * @param action Acción realizada
     * @param detail Detalles adicionales del registro
     * @param date Fecha y hora del registro
     */
    public Log(String email, String action, String detail, Date date) {
        this.email = email;
        this.action = action;
        this.detail = detail;
        this.date = date;
    }

    /**
     * Método getter para obtener el correo electrónico asociado al registro.
     * @return Correo electrónico asociado al registro
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método getter para obtener la acción realizada.
     * @return Acción realizada
     */
    public String getAction() {
        return action;
    }

    /**
     * Método getter para obtener los detalles adicionales del registro.
     * @return Detalles adicionales del registro
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Método getter para obtener la fecha y hora del registro.
     * @return Fecha y hora del registro
     */
    public Date getDate() {
        return date;
    }
}
