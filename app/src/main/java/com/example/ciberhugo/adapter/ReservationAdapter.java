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

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    private List<Reservation> reservations;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;

    public ReservationAdapter(List<Reservation> reservations, Context context) {
        this.reservations = reservations;
        this.context = context;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        holder.dateTextView.setText(reservation.getFormattedDate());
        holder.actionTextView.setText(reservation.getReason());
        holder.timeTextView.setText(reservation.getTime());

        holder.editButton.setOnClickListener(v -> showDateTimePicker(holder, reservation));

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Reserva")
                    .setMessage("¿Estás seguro de que deseas eliminar esta reserva?")
                    .setPositiveButton("Sí", (dialog, which) -> deleteReservation(reservation))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    private void showDateTimePicker(ReservationViewHolder holder, Reservation reservation) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(holder.itemView.getContext(), (view, year, month, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(holder.itemView.getContext(), (timeView, hourOfDay, minute) -> {
                String newDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                String newTime = String.format("%02d:%02d", hourOfDay, minute);
                updateReservationDateTime(reservation, newDate, newTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateReservationDateTime(Reservation reservation, String newDate, String newTime) {
        db.collection("reservations").document(reservation.getId())
                .update("date", newDate, "time", newTime)
                .addOnSuccessListener(aVoid -> {
                    reservation.setDate(newDate);
                    reservation.setTime(newTime);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Fecha y hora actualizadas", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al actualizar fecha y hora", Toast.LENGTH_SHORT).show());
    }

    private void deleteReservation(Reservation reservation) {
        db.collection("reservations").document(reservation.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    reservations.remove(reservation);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar la reserva", Toast.LENGTH_SHORT).show());
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImageView;
        TextView dateTextView;
        TextView actionTextView;
        TextView timeTextView;
        Button editButton;
        Button deleteButton;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.logoImageView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            actionTextView = itemView.findViewById(R.id.actionTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
