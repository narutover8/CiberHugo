/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.view;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ciberhugo.R;
import com.example.ciberhugo.adapter.ReservationAdapter;
import com.example.ciberhugo.model.Reservation;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad para que los administradores gestionen las reservas.
 * Permite visualizar todas las reservas almacenadas en Firestore.
 */
public class AdminManageReservationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter reservationAdapter;
    private List<Reservation> reservationList;
    private FirebaseFirestore db;
    private Button buttonBackToMenu;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_reservations);

        // Inicialización del RecyclerView y del objeto de FirebaseFirestore
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();

        // Cargar las reservas desde Firestore al iniciar la actividad
        loadReservations();

        // Configuración del SwipeRefreshLayout para actualizar las reservas al deslizar hacia abajo
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadReservations);

        // Configuración del botón para regresar al menú principal
        buttonBackToMenu = findViewById(R.id.button_back);
        buttonBackToMenu.setOnClickListener(v -> finish());

        // Inicialización del adaptador de reservas y asignación al RecyclerView
        reservationAdapter = new ReservationAdapter(reservationList, this);
        recyclerView.setAdapter(reservationAdapter);
    }

    /**
     * Método para cargar las reservas desde Firestore y actualizar la lista en el RecyclerView.
     */
    private void loadReservations() {
        reservationList = new ArrayList<>();
        db.collection("reservations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Obtener los datos de cada reserva y crear un objeto Reservation
                    Reservation reservation = new Reservation(
                            document.getId(),
                            document.getString("email"),
                            document.getString("reason"),
                            document.getString("date"),
                            document.getString("time"),
                            document.getString("people")
                    );
                    reservationList.add(reservation); // Agregar la reserva a la lista
                }
                reservationAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            } else {
            }
        });
    }
}
