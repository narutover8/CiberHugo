/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

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

    // Elementos de la interfaz de usuario (UI)
    private TextView timeCounter;
    private TextView welcomeText;
    private Button btnLogout;
    private Button btnViewReservation;
    private Button btnGoToHtml;

    // Instancia de la base de datos Firebase Firestore
    private FirebaseFirestore db;
    private String email;  // Email del usuario
    private static final String TAG = "HomeFragment";  // Etiqueta para logs
    private CountDownTimer countDownTimer;  // Temporizador para la cuenta regresiva

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar los elementos de la interfaz de usuario
        timeCounter = view.findViewById(R.id.time_counter);
        welcomeText = view.findViewById(R.id.welcome_text);
        btnLogout = view.findViewById(R.id.button_logout);
        btnViewReservation = view.findViewById(R.id.button_reserve);
        btnGoToHtml = view.findViewById(R.id.button_website);

        // Inicializar la instancia de Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener el email pasado como argumento a este fragmento
        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        // Si el email no es nulo, obtener los detalles del usuario desde Firestore
        if (email != null) {
            getUserDetails();
        } else {
            timeCounter.setText("Error: Email not found");
        }

        // Configurar los listeners para los botones
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();  // Llamar a onStop para guardar el tiempo restante y cancelar el temporizador
                Intent intentLogOut = new Intent(getActivity(), Login.class);  // Iniciar la actividad de login
                startActivity(intentLogOut);
            }
        });

        btnViewReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReserv = new Intent(getActivity(), ReservationActivity.class);  // Iniciar la actividad de reservaciones
                intentReserv.putExtra("email", email);  // Pasar el email a la actividad de reservaciones
                startActivity(intentReserv);
            }
        });

        btnGoToHtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://villodreshugo.wixsite.com/ciberhugo";
                Uri link = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);  // Abrir la URL en un navegador web
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        updateRemainingTime();  // Guardar el tiempo restante en Firestore
        if (countDownTimer != null) {
            countDownTimer.cancel();  // Cancelar el temporizador de cuenta regresiva
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateRemainingTime();  // Guardar el tiempo restante en Firestore
        if (countDownTimer != null) {
            countDownTimer.cancel();  // Cancelar el temporizador de cuenta regresiva
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimeCounter();  // Actualizar el contador de tiempo al reanudar el fragmento
    }

    /**
     * Método para obtener los detalles del usuario desde Firestore.
     */
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

    /**
     * Método para guardar el tiempo de inicio en las preferencias compartidas.
     * @param startTimeInSeconds Tiempo de inicio en segundos
     */
    private void saveStartTimeToPreferences(long startTimeInSeconds) {
        Context context = getActivity();
        if (context != null) {
            context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putLong("startTime", startTimeInSeconds)
                    .apply();
        }
    }

    /**
     * Método para iniciar la cuenta regresiva del tiempo restante.
     * @param timeLeftInSeconds Tiempo restante en segundos
     */
    private void startCountDown(long timeLeftInSeconds) {
        long timeLeftInMillis = timeLeftInSeconds * 1000;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
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

    /**
     * Método para actualizar el contador de tiempo al reanudar el fragmento.
     */
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

    /**
     * Método para actualizar el tiempo restante en la base de datos.
     */
    private void updateRemainingTime() {
        String timeString = timeCounter.getText().toString();
        long timeLeftInSeconds = parseTimeStringToSeconds(timeString);
        updateRemainingTimeInDatabase(email, timeLeftInSeconds);
    }

    /**
     * Método para convertir una cadena de tiempo en formato HH:mm:ss a segundos.
     * @param timeString Cadena de tiempo en formato HH:mm:ss
     * @return Tiempo en segundos
     */
    private long parseTimeStringToSeconds(String timeString) {
        String[] parts = timeString.split(":");
        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        long seconds = Long.parseLong(parts[2]);
        return (hours * 3600) + (minutes * 60) + seconds;
    }

    /**
     * Método para actualizar el tiempo restante en la base de datos Firestore.
     * @param email Email del usuario
     * @param timeLeftInSeconds Tiempo restante en segundos
     */
    private void updateRemainingTimeInDatabase(String email, long timeLeftInSeconds) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String documentId = document.getId();
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
