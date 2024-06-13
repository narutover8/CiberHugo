/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ciberhugo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Actividad para que los administradores puedan prohibir a usuarios específicos.
 * Verifica la existencia del correo electrónico en Firestore y actualiza el estado de prohibición.
 */
public class AdminBanUsersActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button buttonBanUser;
    private Button buttonBackToMenu;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ban_users);

        // Inicialización de vistas y Firebase Firestore
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonBanUser = findViewById(R.id.buttonBanUser);
        buttonBackToMenu = findViewById(R.id.buttonBackToMenu);
        db = FirebaseFirestore.getInstance();

        // Configuración del botón para prohibir usuario
        buttonBanUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(AdminBanUsersActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                } else {
                    // Verificar si el correo electrónico existe y está baneado
                    checkIfEmailExists(email, new EmailCheckCallback() {
                        @Override
                        public void onEmailCheckCompleted(boolean emailExists, boolean isBanned) {
                            if (emailExists) {
                                if (isBanned) {
                                    Toast.makeText(AdminBanUsersActivity.this, "User is already banned", Toast.LENGTH_SHORT).show();
                                } else {
                                    banUser(email); // Prohibir al usuario si no está baneado
                                }
                            } else {
                                Toast.makeText(AdminBanUsersActivity.this, "User with email " + email + " does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        // Configuración del botón para regresar al menú principal
        buttonBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Método para prohibir a un usuario actualizando el campo "banned" en Firestore.
     * @param email Correo electrónico del usuario a prohibir.
     */
    private void banUser(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                documentSnapshot.getReference().update("banned", true)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(AdminBanUsersActivity.this, "User banned successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(AdminBanUsersActivity.this, "Error banning user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(AdminBanUsersActivity.this, "Error checking email existence", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Método para verificar si un correo electrónico existe en Firestore y si está baneado.
     * @param email Correo electrónico a verificar.
     * @param callback Interfaz de callback para manejar el resultado de la verificación.
     */
    private void checkIfEmailExists(String email, final EmailCheckCallback callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean emailExists = !task.getResult().isEmpty();
                            boolean isBanned = false;
                            if (emailExists) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                // Verificar si el usuario está baneado
                                isBanned = documentSnapshot.getBoolean("banned") != null && documentSnapshot.getBoolean("banned");
                            }
                            // Llamar al callback con los resultados de la verificación
                            callback.onEmailCheckCompleted(emailExists, isBanned);
                        } else {
                            // Llamar al callback en caso de error
                            callback.onEmailCheckCompleted(false, false);
                        }
                    }
                });
    }

    // Interfaz para el callback de verificación de correo electrónico
    interface EmailCheckCallback {
        void onEmailCheckCompleted(boolean emailExists, boolean isBanned);
    }
}
