/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciberhugo.R;
import com.example.ciberhugo.model.Log;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para gestionar la lista de registros (logs) en un RecyclerView.
 */
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<Log> logList; // Lista de registros (logs)
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Formato de fecha y hora

    /**
     * Constructor del adaptador.
     * @param logList Lista de registros (logs) a mostrar en el RecyclerView
     */
    public LogAdapter(List<Log> logList) {
        this.logList = logList;
    }

    /**
     * Método llamado cuando se crea un nuevo ViewHolder para un elemento de log.
     * @param parent El ViewGroup al que se añadirá la nueva vista
     * @param viewType El tipo de vista
     * @return Un nuevo LogViewHolder que contiene la vista del elemento de log
     */
    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout del item de log
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(view); // Retornar un nuevo LogViewHolder
    }

    /**
     * Método llamado para mostrar los datos en un ViewHolder específico.
     * @param holder El ViewHolder que debe ser actualizado para representar los contenidos del ítem en la posición dada en el conjunto de datos
     * @param position La posición del ítem dentro del conjunto de datos del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        Log log = logList.get(position); // Obtener el registro (log) en la posición específica
        // Establecer los datos del registro en las vistas del ViewHolder
        holder.emailTextView.setText(log.getEmail());
        holder.actionTextView.setText(log.getAction());
        holder.detailTextView.setText(log.getDetail());

        // Verificar si la fecha no es nula antes de usarla
        if (log.getDate() != null) {
            holder.dateTextView.setText(dateFormat.format(log.getDate())); // Formatear y establecer la fecha en formato específico
        } else {
            holder.dateTextView.setText("No date available"); // Mostrar mensaje si la fecha es nula
        }
    }

    /**
     * Método para obtener el número total de ítems en los datos.
     * @return El número total de registros (logs) en la lista
     */
    @Override
    public int getItemCount() {
        return logList.size(); // Retornar el tamaño de la lista de registros (logs)
    }

    /**
     * Clase interna que representa el ViewHolder para un elemento de log.
     * Contiene referencias a las vistas de la interfaz de usuario para ese elemento.
     */
    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView;
        TextView actionTextView;
        TextView detailTextView;
        TextView dateTextView;

        /**
         * Constructor del ViewHolder.
         * @param itemView La vista inflada que representa un elemento de log
         */
        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar las referencias a las vistas dentro del itemView
            emailTextView = itemView.findViewById(R.id.log_email);
            actionTextView = itemView.findViewById(R.id.log_action);
            detailTextView = itemView.findViewById(R.id.log_detail);
            dateTextView = itemView.findViewById(R.id.log_date);
        }
    }
}
