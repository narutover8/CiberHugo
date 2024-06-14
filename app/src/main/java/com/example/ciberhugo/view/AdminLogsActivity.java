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
import com.example.ciberhugo.adapter.LogAdapter;
import com.example.ciberhugo.model.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Actividad para que los administradores visualicen registros (logs) de actividades.
 * Muestra los logs almacenados en Firestore.
 */
public class AdminLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLogs;
    private LogAdapter logAdapter;
    private List<Log> logList = new ArrayList<>();
    private FirebaseFirestore db;
    private Button buttonBackLog;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_logs);

        // Inicialización del RecyclerView y configuración del LinearLayoutManager
        recyclerViewLogs = findViewById(R.id.recyclerViewLogs);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this));

        // Inicialización del adaptador de logs y asignación al RecyclerView
        logAdapter = new LogAdapter(logList);
        recyclerViewLogs.setAdapter(logAdapter);

        // Configuración del SwipeRefreshLayout para actualizar los logs al deslizar hacia abajo
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadLogs);

        // Configuración del botón para regresar al menú principal
        buttonBackLog = findViewById(R.id.button_back);
        buttonBackLog.setOnClickListener(v -> finish());

        // Obtención de la instancia de FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Cargar los logs desde Firestore al iniciar la actividad
        loadLogs();
    }

    /**
     * Método para cargar los logs desde Firestore y actualizar la lista en el RecyclerView.
     */
    private void loadLogs() {
        db.collection("logs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Limpiar la lista de logs antes de cargar nuevos datos
                logList.clear();
                // Iterar sobre los documentos obtenidos y crear objetos Log
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String email = document.getString("email");
                    String action = document.getString("action");
                    String detail = document.getString("detail");
                    Date date = document.getDate("date");

                    // Crear un objeto Log y agregarlo a la lista
                    Log log = new Log(email, action, detail, date);
                    logList.add(log);
                }
                // Ordenar la lista de logs por fecha de forma descendente
                Collections.sort(logList, new Comparator<Log>() {
                    @Override
                    public int compare(Log log1, Log log2) {
                        return log2.getDate().compareTo(log1.getDate());
                    }
                });
                // Notificar al adaptador que los datos han cambiado
                logAdapter.notifyDataSetChanged();
            } else {
                // Manejar errores si la carga de logs falla
                Toast.makeText(AdminLogsActivity.this, "Error getting logs: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
            // Finalizar el indicador de actualización (SwipeRefreshLayout)
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}
