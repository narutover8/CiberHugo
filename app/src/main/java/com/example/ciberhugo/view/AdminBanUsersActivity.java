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

public class AdminBanUsersActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button buttonBanUser;
    private Button buttonBackToMenu;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ban_users);

        editTextEmail = findViewById(R.id.editTextEmail);
        buttonBanUser = findViewById(R.id.buttonBanUser);
        buttonBackToMenu = findViewById(R.id.buttonBackToMenu);
        db = FirebaseFirestore.getInstance();

        buttonBanUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(AdminBanUsersActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                } else {
                    checkIfEmailExists(email, new EmailCheckCallback() {
                        @Override
                        public void onEmailCheckCompleted(boolean emailExists, boolean isBanned) {
                            if (emailExists) {
                                if (isBanned) {
                                    Toast.makeText(AdminBanUsersActivity.this, "User is already banned", Toast.LENGTH_SHORT).show();
                                } else {
                                    banUser(email);
                                }
                            } else {
                                Toast.makeText(AdminBanUsersActivity.this, "User with email " + email + " does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        buttonBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

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
                                isBanned = documentSnapshot.getBoolean("banned") != null && documentSnapshot.getBoolean("banned");
                            }
                            callback.onEmailCheckCompleted(emailExists, isBanned);
                        } else {
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
