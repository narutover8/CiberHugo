package com.example.ciberhugo.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciberhugo.R;
import com.example.ciberhugo.adapter.LogAdapter;
import com.example.ciberhugo.model.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLogs;
    private LogAdapter logAdapter;
    private List<Log> logList = new ArrayList<>();
    private FirebaseFirestore db;
    private Button buttonBackLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_logs);

        recyclerViewLogs = findViewById(R.id.recyclerViewLogs);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this));

        logAdapter = new LogAdapter(logList);
        recyclerViewLogs.setAdapter(logAdapter);

        buttonBackLog = findViewById(R.id.button_back);
        buttonBackLog.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        loadLogs();
    }

    private void loadLogs() {
        db.collection("logs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        logList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            String action = document.getString("action");
                            String detail = document.getString("detail");
                            Date date = document.getDate("date");

                            Log log = new Log(email, action, detail, date);
                            logList.add(log);
                        }
                        logAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AdminLogsActivity.this, "Error getting logs: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
