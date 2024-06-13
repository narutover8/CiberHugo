/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ciberhugo.R;
import com.example.ciberhugo.adapter.UserAdapter;
import com.example.ciberhugo.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Actividad para administrar y modificar usuarios desde la perspectiva de un administrador.
 */
public class AdminModifyUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btnBackToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_modify_users);

        // Inicialización de RecyclerView y SwipeRefreshLayout
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadUsers);

        // Adaptador para el RecyclerView de usuarios
        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setAdapter(userAdapter);

        // Instancia de FirebaseFirestore para acceder a la base de datos Firestore
        db = FirebaseFirestore.getInstance();

        // Cargar la lista de usuarios al iniciar la actividad
        loadUsers();

        // Configuración del botón para regresar al menú principal
        btnBackToMenu = findViewById(R.id.button_back);
        btnBackToMenu.setOnClickListener(v -> finish());
    }

    /**
     * Método para cargar la lista de usuarios desde Firestore.
     * Se filtran los usuarios para mostrar solo aquellos que no son administradores.
     * Se maneja la actualización visual mediante SwipeRefreshLayout.
     */
    private void loadUsers() {
        swipeRefreshLayout.setRefreshing(true);
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful()) {
                        List<User> filteredUserList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String email = document.getString("email");
                            String username = document.getString("user");
                            String phone = document.getString("phone");

                            Boolean isAdminObj = document.getBoolean("admin");
                            boolean admin = (isAdminObj != null) && isAdminObj;

                            // Validar que el campo "timeLeft" contenga solo números
                            Long timeLeft = 0L;
                            if (document.contains("timeLeft")) {
                                Object timeLeftObj = document.get("timeLeft");
                                if (timeLeftObj instanceof Long) {
                                    timeLeft = (Long) timeLeftObj;
                                } else if (timeLeftObj instanceof Integer) {
                                    timeLeft = ((Integer) timeLeftObj).longValue();
                                } else {
                                    // Handle unexpected data type for "timeLeft"
                                    timeLeft = 0L; // Set default value or handle accordingly
                                }
                            }

                            // Validar si el usuario cumple con los mismos requisitos que en el registro
                            if (isValidUser(email, username, phone)) {
                                User user = new User(id, email, username, phone, admin, timeLeft);
                                if (!admin) {
                                    filteredUserList.add(user);
                                }
                            }
                        }
                        // Actualizar la lista de usuarios en el RecyclerView
                        userList.clear();
                        userList.addAll(filteredUserList);
                        userAdapter.notifyDataSetChanged();
                    } else {
                        // Mostrar mensaje de error en caso de fallo al obtener usuarios
                        Toast.makeText(AdminModifyUsersActivity.this, "Error getting users: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Método para validar que el usuario cumpla con los requisitos del registro:
     * - Email válido.
     * - Nombre de usuario con al menos 4 caracteres.
     * - Número de teléfono numérico con al menos 9 dígitos.
     *
     * @param email    Email del usuario.
     * @param username Nombre de usuario del usuario.
     * @param phone    Número de teléfono del usuario.
     * @return true si el usuario cumple con los requisitos, false de lo contrario.
     */
    private boolean isValidUser(String email, String username, String phone) {
        // Validar que el email tenga un formato válido
        if (email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }

        // Validar que el username no esté vacío y tenga al menos 4 caracteres
        if (username == null || username.length() < 4) {
            return false;
        }

        // Validar que el teléfono sea numérico y tenga al menos 9 dígitos
        if (phone == null || !Pattern.matches("[0-9]+", phone) || phone.length() < 9) {
            return false;
        }

        return true;
    }
}
