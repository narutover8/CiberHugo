package com.example.ciberhugo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciberhugo.R;
import com.example.ciberhugo.adapter.UserAdapter;
import com.example.ciberhugo.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminModifyUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;
    private Button btnBackToMenu, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_modify_users);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();

        loadUsers();

        btnBackToMenu = findViewById(R.id.button_back);
        btnBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave = findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void loadUsers() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            String username = document.getString("user");
                            String phone = document.getString("phone");

                            Boolean isAdminObj = document.getBoolean("isAdmin");
                            boolean isAdmin = (isAdminObj != null) ? isAdminObj : false;

                            Long timeLeftObj = document.getLong("timeLeft");
                            long timeLeft = (timeLeftObj != null) ? timeLeftObj : 0L;

                            User user = new User(email, username, phone, isAdmin, timeLeft);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AdminModifyUsersActivity.this, "Error getting users: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveChanges() {
        for (int i = 0; i < recyclerViewUsers.getChildCount(); i++) {
            View itemView = recyclerViewUsers.getChildAt(i);
            User user = userList.get(i);

            EditText editTextEmail = itemView.findViewById(R.id.editTextEmail);
            EditText editTextPhone = itemView.findViewById(R.id.editTextPhone);
            EditText editTextUsername = itemView.findViewById(R.id.editTextUsername);
            EditText editTextTimeLeft = itemView.findViewById(R.id.editTextTimeLeft);
            CheckBox checkBoxAdmin = itemView.findViewById(R.id.checkBoxAdmin);

            String enteredEmail = editTextEmail.getText().toString().trim();
            String enteredPhone = editTextPhone.getText().toString().trim();
            String enteredUsername = editTextUsername.getText().toString().trim();
            String enteredTimeLeft = editTextTimeLeft.getText().toString().trim();
            boolean enteredIsAdmin = checkBoxAdmin.isChecked();

            if (!enteredEmail.isEmpty()) {
                user.setEmail(enteredEmail);
            }

            if (!enteredPhone.isEmpty()) {
                user.setPhone(enteredPhone);
            }

            if (!enteredUsername.isEmpty()) {
                user.setUsername(enteredUsername);
            }

            if (!enteredTimeLeft.isEmpty()) {
                long timeLeftInHours = Long.parseLong(enteredTimeLeft);
                long timeLeftInSeconds = timeLeftInHours * 3600;
                user.setTimeLeft((int) timeLeftInSeconds);
            }

            user.setAdmin(enteredIsAdmin);

            db.collection("users").document(user.getEmail())
                    .update("email", user.getEmail(),
                            "user", user.getUsername(),
                            "phone", user.getPhone(),
                            "timeLeft", user.getTimeLeft(),
                            "isAdmin", user.isAdmin())
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar cambios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        userAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
    }
}
