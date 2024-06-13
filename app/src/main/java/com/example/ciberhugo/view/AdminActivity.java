/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

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

/**
 * Actividad principal para la funcionalidad de administrador.
 * Permite gestionar diferentes acciones a través de un menú lateral.
 */
public class AdminActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button btnOpenDrawer;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Inicialización de vistas
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        btnOpenDrawer = findViewById(R.id.button_open_drawer);
        btnLogout = findViewById(R.id.button_logoutAdmin);

        // Configuración del toggle para abrir y cerrar el drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Listener para abrir y cerrar el drawer al hacer clic en el botón
        btnOpenDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        // Listener para el botón de logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes agregar cualquier lógica adicional antes de cerrar sesión, como actualizar datos en la base de datos
                onStop();

                // Iniciar la actividad Login para cerrar sesión
                Intent intentLogOut = new Intent(AdminActivity.this, Login.class);
                startActivity(intentLogOut);
            }
        });

        // Listener para los elementos del menú de navegación
        navigationView.findViewById(R.id.button_logs).setOnClickListener(this::onMenuItemClick);
        navigationView.findViewById(R.id.button_manage_reservations).setOnClickListener(this::onMenuItemClick);
        navigationView.findViewById(R.id.button_modify_users).setOnClickListener(this::onMenuItemClick);
        navigationView.findViewById(R.id.button_ban_users).setOnClickListener(this::onMenuItemClick);
    }

    /**
     * Método para manejar el clic en los elementos del menú de navegación.
     * @param view Vista del elemento del menú que fue clicado.
     */
    private void onMenuItemClick(View view) {
        int id = view.getId();

        // Según el ID del elemento clicado, iniciar la actividad correspondiente
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

        // Cerrar el drawer después de seleccionar un elemento del menú
        drawerLayout.closeDrawer(navigationView);
    }
}
