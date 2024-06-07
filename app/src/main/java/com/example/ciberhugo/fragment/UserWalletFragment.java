package com.example.ciberhugo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ciberhugo.R;
import com.example.ciberhugo.controller.FirebaseDBConnection;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserWalletFragment extends Fragment {

    private FirebaseFirestore db;
    private RadioGroup radioGroupHours;
    private Button buttonBuy;
    private FirebaseDBConnection firebaseDBConnection;

    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        db = FirebaseFirestore.getInstance();
        firebaseDBConnection = new FirebaseDBConnection();

        radioGroupHours = view.findViewById(R.id.radioGroupHours);
        buttonBuy = view.findViewById(R.id.button_buy);

        // Assuming userEmail is passed through arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            userEmail = arguments.getString("email");
        }

        buttonBuy.setOnClickListener(v -> onBuyButtonClicked());

        return view;
    }

    private void onBuyButtonClicked() {
        int selectedId = radioGroupHours.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(getContext(), "Selecciona una opción de horas", Toast.LENGTH_SHORT).show();
            return;
        }

        int hoursToBuy = 0;
        double totalSpent = 0.0;
        int secondsToAdd = 0;

        if (selectedId == R.id.radio_1h) {
            hoursToBuy = 1;
            totalSpent = 2.0;
            secondsToAdd = 3600;
        } else if (selectedId == R.id.radio_3h) {
            hoursToBuy = 3;
            totalSpent = 5.0;
            secondsToAdd = 3 * 3600;
        } else if (selectedId == R.id.radio_5h) {
            hoursToBuy = 5;
            totalSpent = 8.0;
            secondsToAdd = 5 * 3600;
        } else if (selectedId == R.id.radio_10h) {
            hoursToBuy = 10;
            totalSpent = 10.0;
            secondsToAdd = 10 * 3600;
        }

        saveOrder(userEmail, hoursToBuy, totalSpent, secondsToAdd);
    }

    private void saveOrder(String email, int hoursToBuy, double totalSpent, int secondsToAdd) {
        // Crear un mapa para almacenar los datos de la orden
        Map<String, Object> order = new HashMap<>();
        order.put("order_name", "Compra de horas");
        order.put("quantity", hoursToBuy);
        order.put("prize", totalSpent);

        // Agregar la orden a la colección "users" en Firestore
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String userId = task.getResult().getDocuments().get(0).getId();
                        db.collection("users").document(userId)
                                .update("Order", FieldValue.arrayUnion(order), "timeLeft", FieldValue.increment(secondsToAdd))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Compra realizada con éxito", Toast.LENGTH_SHORT).show();
                                    firebaseDBConnection.insertLog(email, "Compra de horas", "Compra de " + hoursToBuy + " hora/s por " + totalSpent + " €");
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al realizar la compra", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
