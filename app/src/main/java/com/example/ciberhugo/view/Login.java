package com.example.ciberhugo.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ciberhugo.R;
import com.example.ciberhugo.adapter.CustomSpinnerAdapter;
import com.example.ciberhugo.controller.FirebaseDBConnection;
import com.example.ciberhugo.fragment.LogsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    private Spinner languageSpinner;
    private FirebaseAuth mAuth; // Declaración de la instancia de FirebaseAuth
    private TextView urlToRegister;
    private Button loginButton;
    private FirebaseFirestore db;
    private FirebaseDBConnection firebaseDBConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        firebaseDBConnection = new FirebaseDBConnection();

        loginButton = findViewById(R.id.loginButton);

        db = FirebaseFirestore.getInstance();

        // Inicialización de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        urlToRegister = findViewById(R.id.registerLinkTextView);

        urlToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí iniciamos la actividad de registro
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        languageSpinner = findViewById(R.id.languageSpinner);

        LogsFragment[] languageItems = {
                new LogsFragment(R.drawable.icoselect, "Ninguno"),
                new LogsFragment(R.drawable.icoesp, "Español"),
                new LogsFragment(R.drawable.icoeng, "English")
        };

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, Arrays.asList(languageItems));
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogsFragment selectedLanguage = (LogsFragment) parent.getItemAtPosition(position);
                if (selectedLanguage.getLanguageId().equalsIgnoreCase("Español") || selectedLanguage.getLanguageId().equalsIgnoreCase("Spanish")) {
                    setLocale("es");
                } else if (selectedLanguage.getLanguageId().equalsIgnoreCase("Inglés") || selectedLanguage.getLanguageId().equalsIgnoreCase("English")) {
                    setLocale("en");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtenemos el correo electrónico y la contraseña de los campos de texto
                EditText emailEditText = findViewById(R.id.emailEditText);
                EditText passwordEditText = findViewById(R.id.passwordEditText);
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Llamamos al método signIn() con el correo electrónico y la contraseña
                signIn(email, password);
            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        // Creamos un Intent para iniciar la actividad Login
        Intent refresh = new Intent(this, Login.class);
        finish();
        startActivity(refresh);
    }

    private void signIn(String email, String password) {
        // Obtenemos el hash de la contraseña introducida por el usuario
        String hashedPassword = hashPassword(password);

        // Realizamos la autenticación con el correo electrónico y el hash de la contraseña
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", hashedPassword)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // No se encontró ningún usuario con las credenciales proporcionadas
                                Log.d(TAG, "No se encontró ningún usuario con las credenciales proporcionadas.");
                                Toast.makeText(Login.this, "Correo electrónico o contraseña incorrectos.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Autenticación exitosa
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Verificar si el usuario es administrador
                                DocumentSnapshot userDocument = task.getResult().getDocuments().get(0);
                                boolean isAdmin = userDocument.getBoolean("admin");
                                boolean isBanned = userDocument.getBoolean("banned");

                                if (isBanned) {
                                    // El usuario está prohibido, mostrar un mensaje y evitar el inicio de sesión
                                    Log.d(TAG, "El usuario está baneado.");
                                    Toast.makeText(Login.this, "Este usuario está baneado.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // El usuario no está prohibido, permitir el inicio de sesión y redirigir según el rol
                                    // Guardar en el log que el usuario ha iniciado sesión
                                    Log.d(TAG, "Usuario logueado: " + email + " - Admin: " + isAdmin);

                                    // Redirigir a la actividad correspondiente
                                    if (isAdmin) {
                                        Intent intent = new Intent(Login.this, AdminActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(Login.this, QrAuth.class);
                                        intent.putExtra("userId", email); // Pasar el correo electrónico
                                        startActivity(intent);
                                    }
                                }
                            }
                        } else {
                            Log.w(TAG, "Error al realizar la autenticación.", task.getException());
                            Toast.makeText(Login.this, "Error al realizar la autenticación.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

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
}
