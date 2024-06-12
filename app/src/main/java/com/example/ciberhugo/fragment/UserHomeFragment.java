package com.example.ciberhugo.fragment;

import android.content.Context;
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
import androidx.core.content.ContextCompat;
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
                String url = "https://villodreshugo.wixsite.com/ciberhugo";
                Uri link = Uri.parse(url);

            Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);

            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        updateRemainingTime();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateRemainingTime();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimeCounter();
    }

    private void getUserDetails() {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Long timeLeft = document.getLong("timeLeft");
                        String username = document.getString("user");
                        if (username != null) {
                            welcomeText.setText("Bienvenido, " + username);
                        } else {
                            welcomeText.setText("Bienvenido, Usuario");
                        }
                        if (timeLeft != null) {
                            long currentTimeInSeconds = System.currentTimeMillis() / 1000;
                            db.collection("users").document(document.getId())
                                    .update("startTime", currentTimeInSeconds);
                            saveStartTimeToPreferences(currentTimeInSeconds);
                            startCountDown(timeLeft);
                        } else {
                            timeCounter.setText("Error: timeLeft not found");
                        }
                    } else {
                        timeCounter.setText("Error: User not found");
                    }
                });
    }

    private void saveStartTimeToPreferences(long startTimeInSeconds) {
        Context context = getActivity();
        if (context != null) {
            context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putLong("startTime", startTimeInSeconds)
                    .apply();
        }
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
                timeCounter.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                Toast.makeText(getActivity(), "Tu tiempo ha terminado, por favor compra más horas.", Toast.LENGTH_LONG).show();

            }
        }.start();
    }

    private void updateTimeCounter() {
        Context context = getActivity();
        if (context != null) {
            long startTimeInSeconds = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    .getLong("startTime", -1);
            if (startTimeInSeconds != -1) {
                long currentTimeInSeconds = System.currentTimeMillis() / 1000;
                long elapsedTimeInSeconds = currentTimeInSeconds - startTimeInSeconds;

                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                Long originalTimeLeft = document.getLong("timeLeft");
                                if (originalTimeLeft != null) {
                                    long newTimeLeftInSeconds = originalTimeLeft - elapsedTimeInSeconds;
                                    if (newTimeLeftInSeconds <= 0) {
                                        timeCounter.setText("00:00:00");
                                        Toast.makeText(getActivity(), "Tu tiempo ha terminado, por favor compra más horas.", Toast.LENGTH_LONG).show();
                                    } else {
                                        startCountDown(newTimeLeftInSeconds);
                                    }
                                }
                            }
                        });
            }
        }
    }

    private void updateRemainingTime() {
        // Obtener el tiempo restante del TextView
        String timeString = timeCounter.getText().toString();
        long timeLeftInSeconds = parseTimeStringToSeconds(timeString);

        // Actualizar el tiempo restante en la base de datos
        updateRemainingTimeInDatabase(email, timeLeftInSeconds);
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
