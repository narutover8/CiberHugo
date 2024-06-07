package com.example.ciberhugo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ciberhugo.R;
import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button btnOpenDrawer;
    private Button btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        btnOpenDrawer = findViewById(R.id.button_open_drawer);
        btnLogout = findViewById(R.id.button_logoutAdmin);


        // Set a toggle button for the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        btnOpenDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Actualizar el tiempo restante en la base de datos antes de iniciar la actividad Login
                onStop();

                // Iniciar la actividad Login
                Intent intentLogOut = new Intent(AdminActivity.this, Login.class);
                startActivity(intentLogOut);
            }
        });


        // Set navigation item click listeners
        navigationView.findViewById(R.id.button_logs).setOnClickListener(this::onMenuItemClick);
        navigationView.findViewById(R.id.button_manage_reservations).setOnClickListener(this::onMenuItemClick);
        navigationView.findViewById(R.id.button_modify_users).setOnClickListener(this::onMenuItemClick);
        navigationView.findViewById(R.id.button_ban_users).setOnClickListener(this::onMenuItemClick);
    }

    private void onMenuItemClick(View view) {
        int id = view.getId();

        if (id == R.id.button_logs) {
            Intent intent = new Intent(AdminActivity.this, AdminLogsActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_manage_reservations) {
            Intent intent = new Intent(AdminActivity.this, AdminManageReservationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_modify_users) {
            Intent intent = new Intent(AdminActivity.this, AdminModifyUsersActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_ban_users) {
            Intent intent = new Intent(AdminActivity.this, AdminBanUsersActivity.class);
            startActivity(intent);
        }

        // Close the drawer after an item is selected
        drawerLayout.closeDrawer(navigationView);
    }
}