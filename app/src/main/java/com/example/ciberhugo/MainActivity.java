/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 * Actividad principal que inicia la aplicación y redirige automáticamente a la actividad de Login.
 */

package com.example.ciberhugo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ciberhugo.view.Login;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Crear un intent para iniciar la actividad Login
        Intent intent = new Intent(MainActivity.this, Login.class);

        // Iniciar la actividad Login
        startActivity(intent);

        // Finalizar la actividad actual para que el usuario no pueda regresar a ella presionando "Atrás"
        finish();
    }
}
