package com.example.ciberhugo.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ciberhugo.R;
import com.example.ciberhugo.view.Login;
import com.example.ciberhugo.view.ReservationActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class UserHomeFragment extends Fragment {

    private TextView timeCounter;
    private TextView welcomeText;
    private FirebaseFirestore db;

    private Button btnLogout;
    private Button btnViewReservation;
    private Button btnGoToHtml;

    private String email;

    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        timeCounter = view.findViewById(R.id.time_counter);
        welcomeText = view.findViewById(R.id.welcome_text);
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        if (email != null) {
            getUserDetails();
        } else {
            timeCounter.setText("Error: Email not found");
        }

        // Initialize buttons
        btnLogout = view.findViewById(R.id.button_logout);
        btnViewReservation = view.findViewById(R.id.button_reserve);
        btnGoToHtml = view.findViewById(R.id.button_website);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Actualizar el tiempo restante en la base de datos antes de iniciar la actividad Login
                onStop();

                // Iniciar la actividad Login
                Intent intentLogOut = new Intent(getActivity(), Login.class);
                startActivity(intentLogOut);
            }
        });

        btnViewReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReserv = new Intent(getActivity(), ReservationActivity.class);
                intentReserv.putExtra("email", email);
                startActivity(intentReserv);
            }
        });

        btnGoToHtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define la URL a la que deseas dirigir al usuario
                String url = "https://www.youtube.com/watch?v=qCm0pMZIQFs";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

                // Comprueba si hay una actividad que pueda manejar este intent
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Si hay una actividad que puede manejar este intent, inicia el intent
                    startActivity(intent);
                } else {
                    // Si no hay actividad que pueda manejar este intent, muestra un mensaje de error
                    Toast.makeText(getActivity(), "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Obtener el tiempo restante del TextView
        String timeString = timeCounter.getText().toString();
        long timeLeftInSeconds = parseTimeStringToSeconds(timeString);

        // Actualizar el tiempo restante en la base de datos
        updateRemainingTimeInDatabase(email, timeLeftInSeconds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Obtener el tiempo restante del TextView
        String timeString = timeCounter.getText().toString();
        long timeLeftInSeconds = parseTimeStringToSeconds(timeString);

        // Actualizar el tiempo restante en la base de datos
        updateRemainingTimeInDatabase(email, timeLeftInSeconds);
    }

    private void getUserDetails() {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Long timeLeft = document.getLong("timeLeft");
                        String username = document.getString("user"); // Obtener el nombre del usuario
                        if (username != null) {
                            welcomeText.setText("Bienvenido, " + username); // Establecer el texto de bienvenida
                        } else {
                            welcomeText.setText("Bienvenido, Usuario"); // Texto por defecto si no se encuentra el nombre
                        }
                        if (timeLeft != null) {
                            startCountDown(timeLeft);
                        } else {
                            timeCounter.setText("Error: timeLeft not found");
                        }
                    } else {
                        timeCounter.setText("Error: User not found");
                    }
                });
    }

    private void startCountDown(long timeLeftInSeconds) {
        long timeLeftInMillis = timeLeftInSeconds * 1000;

        new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                timeCounter.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                timeCounter.setText("00:00:00");
            }
        }.start();
    }

    private long parseTimeStringToSeconds(String timeString) {
        String[] parts = timeString.split(":");
        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        long seconds = Long.parseLong(parts[2]);
        return (hours * 3600) + (minutes * 60) + seconds;
    }

    private void updateRemainingTimeInDatabase(String email, long timeLeftInSeconds) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Obtener el ID del documento
                        String documentId = document.getId();
                        // Actualizar el campo "timeLeft" en el documento encontrado
                        db.collection("users")
                                .document(documentId)
                                .update("timeLeft", timeLeftInSeconds)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Time left updated successfully"))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to update time left", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching document", e));
    }
}
