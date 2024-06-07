package com.example.ciberhugo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciberhugo.R;
import com.example.ciberhugo.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
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
        holder.editTextUsername.setText(String.valueOf(user.getTimeLeft()));


        // Convertir segundos a horas
        long timeLeftInHours = user.getTimeLeft() / 3600;
        holder.editTextTimeLeft.setText(String.valueOf(timeLeftInHours));

        holder.checkBoxAdmin.setChecked(user.isAdmin());
        holder.editTextUsername.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        EditText editTextEmail, editTextPhone, editTextUsername, editTextTimeLeft;
        CheckBox checkBoxAdmin;
        ImageView imageViewProfile;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            editTextEmail = itemView.findViewById(R.id.editTextEmail);
            editTextPhone = itemView.findViewById(R.id.editTextPhone);
            editTextUsername = itemView.findViewById(R.id.editTextUsername);
            editTextTimeLeft = itemView.findViewById(R.id.editTextTimeLeft);
            checkBoxAdmin = itemView.findViewById(R.id.checkBoxAdmin);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
        }
    }
}
