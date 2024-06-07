// UserActivity.java
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        // Crear un Bundle para pasar los datos al fragmento
        Bundle bundle = new Bundle();
        bundle.putString("email", email);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();

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

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_content, selectedFragment).commit();
                }

                return true;
            }
        });

        // Selección predeterminada
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}
