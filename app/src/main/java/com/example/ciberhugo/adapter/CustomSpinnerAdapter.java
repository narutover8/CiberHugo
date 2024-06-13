/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.ciberhugo.fragment.LogsFragment;
import com.example.ciberhugo.R;

import java.util.List;

/**
 * Adaptador personalizado para un Spinner que muestra ítems de tipo LogsFragment.
 */
public class CustomSpinnerAdapter extends ArrayAdapter<LogsFragment> {

    /**
     * Constructor del adaptador.
     * @param context Contexto de la aplicación
     * @param items Lista de ítems (LogsFragment) a mostrar en el Spinner
     */
    public CustomSpinnerAdapter(@NonNull Context context, @NonNull List<LogsFragment> items) {
        super(context, 0, items); // Llama al constructor de ArrayAdapter
    }

    /**
     * Método para obtener la vista que se muestra cuando se despliega el Spinner.
     * @param position Posición del ítem en la lista
     * @param convertView Vista reciclada que puede ser reutilizada para mostrar el contenido del ítem
     * @param parent El ViewGroup al que pertenece la vista
     * @return Vista personalizada que muestra el contenido del ítem en la posición especificada
     */
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    /**
     * Método para obtener la vista que se muestra cuando el Spinner está cerrado.
     * @param position Posición del ítem en la lista
     * @param convertView Vista reciclada que puede ser reutilizada para mostrar el contenido del ítem
     * @param parent El ViewGroup al que pertenece la vista
     * @return Vista personalizada que muestra el contenido del ítem en la posición especificada
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    /**
     * Método privado para crear una vista personalizada para un ítem en el Spinner.
     * @param position Posición del ítem en la lista
     * @param convertView Vista reciclada que puede ser reutilizada para mostrar el contenido del ítem
     * @param parent El ViewGroup al que pertenece la vista
     * @return Vista personalizada que muestra el contenido del ítem en la posición especificada
     */
    private View createCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false); // Inflar el layout del ítem del Spinner
        }

        ImageView imageView = convertView.findViewById(R.id.spinnerItemImage); // Obtener la referencia al ImageView dentro del layout

        LogsFragment item = getItem(position); // Obtener el ítem LogsFragment en la posición especificada

        if (item != null) {
            imageView.setImageResource(item.getImageResource()); // Establecer la imagen del ítem en el ImageView
        }

        return convertView; // Retornar la vista personalizada
    }
}
