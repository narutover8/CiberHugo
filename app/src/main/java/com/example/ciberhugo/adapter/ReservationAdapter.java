/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciberhugo.R;
import com.example.ciberhugo.model.Reservation;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;

/**
 * Adaptador para gestionar la lista de reservas en un RecyclerView.
 */
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> reservations; // Lista de reservas
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Instancia de Firestore para operaciones de base de datos
    private Context context; // Contexto de la actividad o fragmento que utiliza este adaptador

    /**
     * Constructor del adaptador.
     * @param reservations Lista de reservas a mostrar en el RecyclerView
     * @param context Contexto de la actividad o fragmento que utiliza este adaptador
     */
    public ReservationAdapter(List<Reservation> reservations, Context context) {
        this.reservations = reservations;
        this.context = context;
    }

    /**
     * Método llamado cuando se crea un nuevo ViewHolder para un elemento de reserva.
     * @param parent El ViewGroup al que se añadirá la nueva vista
     * @param viewType El tipo de vista
     * @return Un nuevo ReservationViewHolder que contiene la vista del elemento de reserva
     */
    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout del item de reserva
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view); // Retornar un nuevo ReservationViewHolder
    }

    /**
     * Método llamado para mostrar los datos en un ViewHolder específico.
     * @param holder El ViewHolder que debe ser actualizado para representar los contenidos del ítem en la posición dada en el conjunto de datos
     * @param position La posición del ítem dentro del conjunto de datos del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position); // Obtener la reserva en la posición específica
        // Establecer los datos de la reserva en los elementos de la vista del ViewHolder
        holder.dateTextView.setText(reservation.getFormattedDate());
        holder.actionTextView.setText(reservation.getReason());
        holder.timeTextView.setText(reservation.getTime());

        // Configurar el evento onClickListener para el botón de editar
        holder.editButton.setOnClickListener(v -> showDateTimePicker(holder, reservation));

        // Configurar el evento onClickListener para el botón de eliminar
        holder.deleteButton.setOnClickListener(v -> {
            // Mostrar un diálogo de confirmación antes de eliminar la reserva
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Reserva")
                    .setMessage("¿Estás seguro de que deseas eliminar esta reserva?")
                    .setPositiveButton("Sí", (dialog, which) -> deleteReservation(reservation))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    /**
     * Método para obtener el número total de ítems en los datos.
     * @return El número total de reservas en la lista
     */
    @Override
    public int getItemCount() {
        return reservations.size(); // Retornar el tamaño de la lista de reservas
    }

    /**
     * Método privado para mostrar el selector de fecha y hora para actualizar la reserva.
     * @param holder El ViewHolder actual para acceder a las vistas
     * @param reservation La reserva que se está actualizando
     */
    private void showDateTimePicker(ReservationViewHolder holder, Reservation reservation) {
        Calendar calendar = Calendar.getInstance(); // Obtener una instancia del calendario actual
        // Mostrar un DatePickerDialog para seleccionar la fecha
        DatePickerDialog datePickerDialog = new DatePickerDialog(holder.itemView.getContext(), (view, year, month, dayOfMonth) -> {
            // Mostrar un TimePickerDialog después de seleccionar la fecha para seleccionar la hora
            TimePickerDialog timePickerDialog = new TimePickerDialog(holder.itemView.getContext(), (timeView, hourOfDay, minute) -> {
                // Formatear la nueva fecha y hora seleccionadas
                String newDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                String newTime = String.format("%02d:%02d", hourOfDay, minute);
                // Llamar al método para actualizar la fecha y hora de la reserva en Firestore
                updateReservationDateTime(reservation, newDate, newTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show(); // Mostrar el diálogo de selección de hora
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show(); // Mostrar el diálogo de selección de fecha
    }

    /**
     * Método privado para actualizar la fecha y hora de la reserva en Firestore.
     * @param reservation La reserva que se está actualizando
     * @param newDate La nueva fecha de la reserva
     * @param newTime La nueva hora de la reserva
     */
    private void updateReservationDateTime(Reservation reservation, String newDate, String newTime) {
        // Actualizar la fecha y hora de la reserva en Firestore
        db.collection("reservations").document(reservation.getId())
                .update("date", newDate, "time", newTime)
                .addOnSuccessListener(aVoid -> {
                    // Actualizar los datos locales de la reserva y notificar al adaptador del cambio
                    reservation.setDate(newDate);
                    reservation.setTime(newTime);
                    notifyDataSetChanged(); // Notificar al RecyclerView que los datos han cambiado
                    Toast.makeText(context, "Fecha y hora actualizadas", Toast.LENGTH_SHORT).show(); // Mostrar mensaje de éxito
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al actualizar fecha y hora", Toast.LENGTH_SHORT).show()); // Mostrar mensaje de error
    }

    /**
     * Método privado para eliminar una reserva de Firestore.
     * @param reservation La reserva que se va a eliminar
     */
    private void deleteReservation(Reservation reservation) {
        // Eliminar la reserva de Firestore
        db.collection("reservations").document(reservation.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Eliminar la reserva de la lista local y notificar al adaptador del cambio
                    reservations.remove(reservation);
                    notifyDataSetChanged(); // Notificar al RecyclerView que los datos han cambiado
                    Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show(); // Mostrar mensaje de éxito
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar la reserva", Toast.LENGTH_SHORT).show()); // Mostrar mensaje de error
    }

    /**
     * Clase interna que representa el ViewHolder para un elemento de reserva.
     * Contiene referencias a las vistas de la interfaz de usuario para ese elemento.
     */
    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImageView;
        TextView dateTextView;
        TextView actionTextView;
        TextView timeTextView;
        Button editButton;
        Button deleteButton;

        /**
         * Constructor del ViewHolder.
         * @param itemView La vista inflada que representa un elemento de reserva
         */
        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar las referencias a las vistas dentro del itemView
            logoImageView = itemView.findViewById(R.id.logoImageView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            actionTextView = itemView.findViewById(R.id.actionTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
