package com.example.ciberhugo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private Button btnBackToMenu;

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
        btnBackToMenu.setOnClickListener(v -> finish());
    }

    private void loadUsers() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String email = document.getString("email");
                            String username = document.getString("user");
                            String phone = document.getString("phone");

                            Boolean isAdminObj = document.getBoolean("isAdmin");
                            boolean isAdmin = (isAdminObj != null) ? isAdminObj : false;

                            Long timeLeftObj = document.getLong("timeLeft");
                            long timeLeft = (timeLeftObj != null) ? timeLeftObj : 0L;

                            User user = new User(id, email, username, phone, isAdmin, timeLeft);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AdminModifyUsersActivity.this, "Error getting users: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
