package com.example.ciberhugo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciberhugo.R;
import com.example.ciberhugo.model.Log;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
    private List<Log> logList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public LogAdapter(List<Log> logList) {
        this.logList = logList;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        Log log = logList.get(position);
        holder.emailTextView.setText(log.getEmail());
        holder.actionTextView.setText(log.getAction());
        holder.detailTextView.setText(log.getDetail());

        // Check if the date is null before using it
        if (log.getDate() != null) {
            holder.dateTextView.setText(dateFormat.format(log.getDate()));
        } else {
            holder.dateTextView.setText("No date available");
        }
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView;
        TextView actionTextView;
        TextView detailTextView;
        TextView dateTextView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.log_email);
            actionTextView = itemView.findViewById(R.id.log_action);
            detailTextView = itemView.findViewById(R.id.log_detail);
            dateTextView = itemView.findViewById(R.id.log_date);
        }
    }
}
