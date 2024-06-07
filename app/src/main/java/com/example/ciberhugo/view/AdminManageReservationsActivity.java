package com.example.ciberhugo.view;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciberhugo.R;
import com.example.ciberhugo.adapter.ReservationAdapter;
import com.example.ciberhugo.model.Reservation;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageReservationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter reservationAdapter;
    private List<Reservation> reservationList;
    private FirebaseFirestore db;
    private Button buttonBackToMenu;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_reservations);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();

        // Cargar reservas desde Firestore
        loadReservations();

        buttonBackToMenu = findViewById(R.id.button_back);
        buttonBackToMenu.setOnClickListener(v -> finish());
        reservationAdapter = new ReservationAdapter(reservationList, this);
        recyclerView.setAdapter(reservationAdapter);
    }

    private void loadReservations() {
        reservationList = new ArrayList<>();
        db.collection("reservations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Reservation reservation = new Reservation(
                            document.getId(),
                            document.getString("email"),
                            document.getString("reason"),
                            document.getString("date"),
                            document.getString("time"),
                            document.getString("people")
                    );
                    reservationList.add(reservation);
                }
                reservationAdapter.notifyDataSetChanged();
            } else {
                // Handle errors
            }
        });
    }
}
