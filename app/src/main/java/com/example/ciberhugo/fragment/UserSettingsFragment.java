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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ciberhugo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Fragmento para gestionar la configuración de usuario, incluyendo cambio de nombre de usuario y contraseña.
 */
public class UserSettingsFragment extends Fragment {

    // Elementos de UI para cambiar nombre de usuario
    private LinearLayout layoutChangeUsername;
    private EditText etPreviousUsername;
    private EditText etNewUsername;
    private Button btnConfirmUsername;

    // Elementos de UI para cambiar contraseña
    private LinearLayout layoutChangePassword;
    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnConfirmPassword;

    // Instancias de Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        layoutChangeUsername = view.findViewById(R.id.layoutChangeUsername);
        etPreviousUsername = view.findViewById(R.id.etPreviousUsername);
        etNewUsername = view.findViewById(R.id.etNewUsername);
        btnConfirmUsername = view.findViewById(R.id.btnConfirmUsername);

        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnConfirmPassword = view.findViewById(R.id.btnConfirmPassword);

        // Configurar clicks de botones
        view.findViewById(R.id.btnChangeUsername).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(layoutChangeUsername);
            }
        });

        view.findViewById(R.id.btnChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(layoutChangePassword);
            }
        });

        btnConfirmUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmUsernameClicked();
            }
        });

        btnConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmPasswordClicked();
            }
        });

        // Obtener y mostrar el nombre de usuario actual
        fetchUsername();

        return view;
    }

    /**
     * Método para cambiar la visibilidad de un layout.
     * @param layout Layout cuya visibilidad se cambiará
     */
    private void toggleVisibility(View layout) {
        if (layout.getVisibility() == View.GONE) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    /**
     * Método para obtener y mostrar el nombre de usuario actual desde Firestore.
     */
    private void fetchUsername() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            String email = arguments.getString("email");
            if (email != null) {
                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                String username = document.getString("user");
                                etPreviousUsername.setText(username);
                            } else {
                                Toast.makeText(getContext(), "Error al obtener el nombre de usuario", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    /**
     * Método para manejar el clic en el botón de confirmar cambio de nombre de usuario.
     */
    private void onConfirmUsernameClicked() {
        String newUsername = etNewUsername.getText().toString().trim();
        Bundle arguments = getArguments();
        if (arguments != null) {
            String email = arguments.getString("email");
            if (!newUsername.isEmpty() && email != null) {
                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                document.getReference().update("user", newUsername)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();
                                            etPreviousUsername.setText(newUsername);
                                            etNewUsername.setText("");
                                            layoutChangeUsername.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        });
            } else {
                Toast.makeText(getContext(), "El nuevo nombre de usuario no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método para manejar el clic en el botón de confirmar cambio de contraseña.
     */
    private void onConfirmPasswordClicked() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(newPassword)) {
            Toast.makeText(getContext(), "La nueva contraseña no cumple con los requisitos de seguridad", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            String email = arguments.getString("email");
            if (email != null) {
                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                String storedPasswordHash = document.getString("password");
                                String currentPasswordHash = hashPassword(currentPassword);

                                if (storedPasswordHash != null && storedPasswordHash.equals(currentPasswordHash)) {
                                    String newHashedPassword = hashPassword(newPassword);
                                    document.getReference().update("password", newHashedPassword)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getContext(), "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                                                etCurrentPassword.setText("");
                                                etNewPassword.setText("");
                                                etConfirmPassword.setText("");
                                                layoutChangePassword.setVisibility(View.GONE);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(getContext(), "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    /**
     * Método para hashear una contraseña usando el algoritmo SHA-256.
     * @param password Contraseña a ser hasheada
     * @return Hash de la contraseña
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }

    /**
     * Método para validar si una contraseña cumple con los requisitos de seguridad.
     * @param password Contraseña a validar
     * @return true si la contraseña es válida, false si no lo es
     */
    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(regex);
    }
}
