package com.example.ciberhugo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ciberhugo.R;
import com.example.ciberhugo.controller.FirebaseDBConnection;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReservationActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private Spinner spinnerPeople;
    private Spinner spinnerReason;
    private Spinner spinnerTime;
    private EditText editTextOtherReason;
    private Button buttonReserve;
    private Button buttonBackToHome;
    private FirebaseFirestore db;
    private String email;

    private FirebaseDBConnection firebaseDBConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservations);

        datePicker = findViewById(R.id.datePicker);
        spinnerPeople = findViewById(R.id.spinnerPeople);
        spinnerReason = findViewById(R.id.spinnerReason);
        spinnerTime = findViewById(R.id.spinnerTime);
        editTextOtherReason = findViewById(R.id.editTextOtherReason);
        buttonReserve = findViewById(R.id.buttonReserve);
        buttonBackToHome = findViewById(R.id.buttonBackToHome);

        db = FirebaseFirestore.getInstance();

        // Inicializar FirebaseDBConnection
        firebaseDBConnection = new FirebaseDBConnection();

        // Obtener el correo electrónico del intent
        email = getIntent().getStringExtra("email");

        spinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedReason = parent.getItemAtPosition(position).toString();
                if (selectedReason.equals("Other") || selectedReason.equals("Otro")) {
                    editTextOtherReason.setVisibility(View.VISIBLE);
                } else {
                    editTextOtherReason.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editTextOtherReason.setVisibility(View.GONE);
            }
        });

        buttonReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReservation();
            }
        });

        buttonBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad UserActivity
                Intent intent = new Intent(ReservationActivity.this, UserActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveReservation() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        String people = spinnerPeople.getSelectedItem().toString();
        String reason = spinnerReason.getSelectedItem().toString();
        String time = spinnerTime.getSelectedItem().toString();

        // Obtener la fecha actual
        Calendar currentDate = Calendar.getInstance();
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1; // Los meses en Calendar van de 0 a 11
        int currentYear = currentDate.get(Calendar.YEAR);

        // Obtener la fecha seleccionada por el usuario
        int selectedDay = datePicker.getDayOfMonth();
        int selectedMonth = datePicker.getMonth() + 1; // Los meses en DatePicker van de 0 a 11
        int selectedYear = datePicker.getYear();

        // Verificar si la fecha seleccionada es anterior a la fecha actual
        if (selectedYear < currentYear ||
                (selectedYear == currentYear && selectedMonth < currentMonth) ||
                (selectedYear == currentYear && selectedMonth == currentMonth && selectedDay < currentDay)) {
            Toast.makeText(this, "No se puede reservar para una fecha anterior a la fecha actual", Toast.LENGTH_SHORT).show();
            return;
        }

        if (reason.equals("Other")) {
            reason = editTextOtherReason.getText().toString().trim();
        }

        if (reason.isEmpty()) {
            Toast.makeText(this, "Por favor, especifica una razón", Toast.LENGTH_SHORT).show();
            return;
        }

        // Formatear la fecha seleccionada
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(selectedYear, selectedMonth - 1, selectedDay);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedDate.getTime());

        // Comprobar si la fecha y la hora ya están reservadas
        String finalReason = reason;
        db.collection("reservations")
                .whereEqualTo("date", formattedDate)
                .whereEqualTo("time", time)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean dateTimeAlreadyReserved = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            dateTimeAlreadyReserved = true;
                            break;
                        }

                        if (dateTimeAlreadyReserved) {
                            Toast.makeText(ReservationActivity.this, "La fecha y hora seleccionadas ya están reservadas", Toast.LENGTH_SHORT).show();
                        } else {
                            // Guardar la reserva si la fecha y la hora no están reservadas
                            Map<String, Object> reservation = new HashMap<>();
                            reservation.put("email", email);
                            reservation.put("date", formattedDate);
                            reservation.put("time", time);
                            reservation.put("people", people);
                            reservation.put("reason", finalReason);

                            db.collection("reservations")
                                    .add(reservation)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(ReservationActivity.this, "Reserva guardada", Toast.LENGTH_SHORT).show();
                                        firebaseDBConnection.insertLog(email, "Reserva realizada", "Reserva realizada correctamente para el usuario con email: " + email);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(ReservationActivity.this, "Fallo al guardar la reserva", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(ReservationActivity.this, "Error al comprobar la disponibilidad de la fecha y hora", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
