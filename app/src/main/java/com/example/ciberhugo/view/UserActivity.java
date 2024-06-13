/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 * UserActivity es la actividad principal para usuarios registrados.
 * Permite navegar entre diferentes fragmentos (Home, Wallet, Settings) mediante
 * un BottomNavigationView y pasa el correo electrónico del usuario a cada fragmento.
 */

package com.example.ciberhugo.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ciberhugo.R;
import com.example.ciberhugo.fragment.UserHomeFragment;
import com.example.ciberhugo.fragment.UserSettingsFragment;
import com.example.ciberhugo.fragment.UserWalletFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class UserActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Obtener la referencia del BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Obtener el intent que inició esta actividad y extraer el correo electrónico del usuario
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        // Crear un Bundle para pasar los datos al fragmento
        Bundle bundle = new Bundle();
        bundle.putString("email", email);

        // Configurar el listener para el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                // Determinar qué fragmento se debe mostrar según la opción seleccionada
                if (id == R.id.navigation_home) {
                    selectedFragment = new UserHomeFragment();
                    selectedFragment.setArguments(bundle); // Pasar el bundle al fragmento
                } else if (id == R.id.navigation_wallet) {
                    selectedFragment = new UserWalletFragment();
                    selectedFragment.setArguments(bundle); // Pasar el bundle al fragmento
                } else if (id == R.id.navigation_settings) {
                    selectedFragment = new UserSettingsFragment();
                    selectedFragment.setArguments(bundle); // Pasar el bundle al fragmento
                }

                // Reemplazar el fragmento en el contenedor principal si se seleccionó uno válido
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_content, selectedFragment).commit();
                }

                return true;
            }
        });

        // Establecer la selección predeterminada en el BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}
