/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

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

/**
 * Fragmento para gestionar la compra de horas de usuario.
 */
public class UserWalletFragment extends Fragment {

    private FirebaseFirestore db;             // Instancia de Firestore
    private RadioGroup radioGroupHours;       // Grupo de botones de radio para seleccionar horas
    private Button buttonBuy;                 // Botón para realizar la compra
    private FirebaseDBConnection firebaseDBConnection;  // Instancia para la conexión a la base de datos

    private String userEmail;                 // Correo electrónico del usuario actual

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño de este fragmento
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        db = FirebaseFirestore.getInstance();  // Inicializar Firestore
        firebaseDBConnection = new FirebaseDBConnection();  // Inicializar la conexión a la base de datos

        radioGroupHours = view.findViewById(R.id.radioGroupHours);  // Obtener referencia al grupo de botones de radio
        buttonBuy = view.findViewById(R.id.button_buy);             // Obtener referencia al botón de compra

        // Obtener el correo electrónico del usuario desde los argumentos del fragmento
        Bundle arguments = getArguments();
        if (arguments != null) {
            userEmail = arguments.getString("email");
        }

        // Configurar el listener para el botón de compra
        buttonBuy.setOnClickListener(v -> onBuyButtonClicked());

        return view;  // Devolver la vista inflada
    }

    /**
     * Método para manejar el clic en el botón de compra.
     */
    private void onBuyButtonClicked() {
        int selectedId = radioGroupHours.getCheckedRadioButtonId();  // Obtener el ID del botón de radio seleccionado

        if (selectedId == -1) {
            Toast.makeText(getContext(), "Selecciona una opción de horas", Toast.LENGTH_SHORT).show();  // Mostrar mensaje si no se seleccionó ninguna opción
            return;
        }

        // Variables para almacenar los detalles de la compra seleccionada
        int hoursToBuy = 0;
        double totalSpent = 0.0;
        int secondsToAdd = 0;

        // Determinar la cantidad de horas, el costo total y los segundos adicionales según la opción seleccionada
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

        // Guardar la orden en la base de datos
        saveOrder(userEmail, hoursToBuy, totalSpent, secondsToAdd);
    }

    /**
     * Método para guardar la orden de compra en Firestore.
     * @param email Correo electrónico del usuario
     * @param hoursToBuy Cantidad de horas a comprar
     * @param totalSpent Total gastado en la compra
     * @param secondsToAdd Segundos a agregar al tiempo actual del usuario
     */
    private void saveOrder(String email, int hoursToBuy, double totalSpent, int secondsToAdd) {
        // Crear un mapa para almacenar los datos de la orden
        Map<String, Object> order = new HashMap<>();
        order.put("order_name", "Compra de horas");
        order.put("quantity", hoursToBuy);
        order.put("prize", totalSpent);

        // Buscar al usuario por su correo electrónico en Firestore
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Obtener el ID del usuario encontrado
                        String userId = task.getResult().getDocuments().get(0).getId();

                        // Actualizar la colección de usuarios con la nueva orden y el tiempo adicional
                        db.collection("users").document(userId)
                                .update("Order", FieldValue.arrayUnion(order), "timeLeft", FieldValue.increment(secondsToAdd))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Compra realizada con éxito", Toast.LENGTH_SHORT).show();
                                    // Registrar la compra en el registro de actividades
                                    firebaseDBConnection.insertLog(email, "Compra de horas", "Compra de " + hoursToBuy + " hora/s por " + totalSpent + " €");
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al realizar la compra", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
