/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 * Register permite a los usuarios registrarse en la aplicación introduciendo su correo electrónico,
 * nombre de usuario, contraseña, número de teléfono y seleccionando el idioma de la interfaz. Verifica
 * la disponibilidad del correo electrónico y número de teléfono antes de registrar al usuario en Firebase Firestore.
 */

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
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ciberhugo.R;
import com.example.ciberhugo.adapter.CustomSpinnerAdapter;
import com.example.ciberhugo.controller.FirebaseDBConnection;
import com.example.ciberhugo.fragment.LogsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

public class Register extends AppCompatActivity {

    Spinner languageSpinner;
    TextView urlToLogin;
    EditText etemail;
    EditText etUsr;
    EditText etPswd;
    EditText etConfPswd;
    EditText etPhoneNumber;
    Button btnRegister;

    FirebaseFirestore db;
    FirebaseDBConnection firebaseDBConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();
        firebaseDBConnection = new FirebaseDBConnection();

        // Inicializar campos de entrada
        etemail = findViewById(R.id.emailEditText);
        etUsr = findViewById(R.id.usernameEditText);
        etPswd = findViewById(R.id.passwordEditText);
        etConfPswd = findViewById(R.id.confirmPasswordEditText);
        etPhoneNumber = findViewById(R.id.phoneEditText);

        // Inicializar botón de registro
        btnRegister = findViewById(R.id.registerButton);

        // Configurar listener para el botón de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener datos de los campos de entrada
                String email = etemail.getText().toString().trim();
                String username = etUsr.getText().toString().trim();
                String password = etPswd.getText().toString().trim();
                String confirmPassword = etConfPswd.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                Integer hours = 0;

                // Validaciones de campos obligatorios
                if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    showAlertDialog("Todos los campos son obligatorios, excepto el teléfono");
                    return;
                }

                // Validación de longitud mínima del nombre de usuario
                if (username.length() < 4) {
                    showAlertDialog("El nombre de usuario debe tener al menos 4 caracteres");
                    return;
                }

                // Validación de caracteres permitidos en el nombre de usuario
                if (!username.matches("^[a-zA-Z0-9]*$")) {
                    showAlertDialog("El nombre de usuario solo puede contener letras y números");
                    return;
                }

                // Validación de formato de correo electrónico
                if (!isValidEmail(email)) {
                    showAlertDialog("Por favor ingrese un correo electrónico válido");
                    return;
                }

                // Validación de formato y complejidad de la contraseña
                if (!isValidPassword(password)) {
                    showAlertDialog("La contraseña debe contener al menos 8 caracteres, incluyendo al menos una letra mayúscula, una letra minúscula, un número y un carácter especial (@#$%^&+=!)");
                    return;
                }

                // Validación de coincidencia de contraseñas
                if (!password.equals(confirmPassword)) {
                    showAlertDialog("Las contraseñas no coinciden");
                    return;
                }

                // Validación de formato de número de teléfono español
                if (!isValidSpanishPhoneNumber(phoneNumber)) {
                    showAlertDialog("Por favor ingrese un número de teléfono válido");
                    return;
                }

                // Verificar si el correo electrónico ya está en uso
                checkIfEmailExists(email, new EmailCheckCallback() {
                    @Override
                    public void onEmailCheckCompleted(boolean emailExists) {
                        if (emailExists) {
                            Toast.makeText(Register.this, "El correo electrónico ya está en uso", Toast.LENGTH_SHORT).show();
                        } else {
                            // Verificar si el número de teléfono ya está en uso
                            checkIfPhoneExists(phoneNumber, new PhoneCheckCallback() {
                                @Override
                                public void onPhoneCheckCompleted(boolean phoneExists) {
                                    if (phoneExists) {
                                        Toast.makeText(Register.this, "El número de teléfono ya está en uso", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Hashear la contraseña antes de registrar al usuario
                                        String hashedPassword = hashPassword(password);

                                        // Insertar usuario en Firebase Firestore
                                        firebaseDBConnection.insertUser(email, username, hashedPassword, phoneNumber,false,false, hours);

                                        // Registrar la acción en FirebaseDBConnection
                                        String logMessage = "El usuario " + username + " se registró exitosamente.";
                                        firebaseDBConnection.insertLog(email, "Usuario Registrado", logMessage);

                                        Toast.makeText(Register.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();

                                        // Redirigir al usuario a la actividad de inicio de sesión
                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        // Configurar listener para el enlace de inicio de sesión
        urlToLogin = findViewById(R.id.loginLinkTextView);
        urlToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir al usuario a la actividad de inicio de sesión
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        // Configurar adaptador y listener para el selector de idioma
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
    }

    // Método para mostrar un cuadro de diálogo con un mensaje
    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No hacer nada, simplemente cerrar el diálogo
                    }
                });
        builder.create().show();
    }

    // Método para establecer el idioma de la aplicación
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, Register.class);
        finish();
        startActivity(refresh);
    }

    // Método para validar la complejidad de la contraseña
    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(regex);
    }

    // Método para hashear la contraseña
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

    // Método para validar el formato de correo electrónico
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void checkIfEmailExists(String email, final EmailCheckCallback callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean emailExists = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                emailExists = true;
                                break;
                            }
                            callback.onEmailCheckCompleted(emailExists);
                        } else {
                            Log.d("Register", "Error checking email existence: ", task.getException());
                            callback.onEmailCheckCompleted(false);
                        }
                    }
                });
    }

    private interface EmailCheckCallback {
        void onEmailCheckCompleted(boolean emailExists);
    }

    public void checkIfPhoneExists(String phone, final PhoneCheckCallback callback) {
        db.collection("users")
                .whereEqualTo("phone", phone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean phoneExists = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                phoneExists = true;
                                break;
                            }
                            callback.onPhoneCheckCompleted(phoneExists);
                        } else {
                            Log.d("FirebaseDBConnection", "Error checking phone existence: ", task.getException());
                            callback.onPhoneCheckCompleted(false);
                        }
                    }
                });
    }

    public interface PhoneCheckCallback {
        void onPhoneCheckCompleted(boolean phoneExists);
    }

    // Método para validar el formato de número de teléfono español
    private boolean isValidSpanishPhoneNumber(String phoneNumber) {
        String regex = "^[6789]\\d{8}$";
        return phoneNumber.matches(regex);
    }
}
