package com.example.ciberhugo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciberhugo.R;
import com.example.ciberhugo.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private FirebaseFirestore db;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.editTextEmail.setText(user.getEmail());
        holder.editTextPhone.setText(user.getPhone());
        holder.editTextUsername.setText(user.getUsername());
        holder.editTextTimeLeft.setText(String.valueOf(user.getTimeLeft() / 3600));
        holder.checkBoxAdmin.setChecked(user.isAdmin());

        holder.btnSave.setOnClickListener(v -> {
            String enteredEmail = holder.editTextEmail.getText().toString().trim();
            String enteredPhone = holder.editTextPhone.getText().toString().trim();
            String enteredUsername = holder.editTextUsername.getText().toString().trim();
            String enteredTimeLeft = holder.editTextTimeLeft.getText().toString().trim();
            boolean enteredIsAdmin = holder.checkBoxAdmin.isChecked();

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
                user.setTimeLeft(timeLeftInSeconds);
            }

            user.setAdmin(enteredIsAdmin);

            db.collection("users").document(user.getId())
                    .update("email", user.getEmail(),
                            "user", user.getUsername(),
                            "phone", user.getPhone(),
                            "timeLeft", user.getTimeLeft(),
                            "isAdmin", user.isAdmin())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(holder.itemView.getContext(), "Cambios guardados para " + user.getUsername(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(holder.itemView.getContext(), "Error al guardar cambios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        EditText editTextEmail, editTextPhone, editTextUsername, editTextTimeLeft;
        CheckBox checkBoxAdmin;
        Button btnSave;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            editTextEmail = itemView.findViewById(R.id.editTextEmail);
            editTextPhone = itemView.findViewById(R.id.editTextPhone);
            editTextUsername = itemView.findViewById(R.id.editTextUsername);
            editTextTimeLeft = itemView.findViewById(R.id.editTextTimeLeft);
            checkBoxAdmin = itemView.findViewById(R.id.checkBoxAdmin);
            btnSave = itemView.findViewById(R.id.buttonSaveUser);
        }
    }
}
