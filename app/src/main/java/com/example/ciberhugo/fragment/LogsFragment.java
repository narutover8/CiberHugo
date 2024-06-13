/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.fragment;

/**
 * Clase para representar un fragmento de registros.
 */
public class LogsFragment {
    private int imageResource; // Recurso de imagen asociado al fragmento
    private String languageId; // Identificador del lenguaje asociado al fragmento

    /**
     * Constructor de la clase LogsFragment.
     * @param imageResource Recurso de imagen del fragmento
     * @param languageId Identificador del lenguaje del fragmento
     */
    public LogsFragment(int imageResource, String languageId) {
        this.imageResource = imageResource;
        this.languageId = languageId;
    }

    /**
     * Método para obtener el recurso de imagen del fragmento.
     * @return Recurso de imagen
     */
    public int getImageResource() {
        return imageResource;
    }

    /**
     * Método para obtener el identificador del lenguaje del fragmento.
     * @return Identificador del lenguaje
     */
    public String getLanguageId() {
        return languageId;
    }
}
