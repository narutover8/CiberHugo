/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

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

/**
 * Adaptador para gestionar la lista de usuarios en un RecyclerView.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList; // Lista de usuarios
    private FirebaseFirestore db; // Instancia de Firestore para operaciones de base de datos

    /**
     * Constructor del adaptador.
     * @param userList Lista de usuarios a mostrar en el RecyclerView
     */
    public UserAdapter(List<User> userList) {
        this.userList = userList;
        this.db = FirebaseFirestore.getInstance(); // Inicialización de Firestore
    }

    /**
     * Método llamado cuando se crea un nuevo ViewHolder para un elemento de la lista.
     * @param parent El ViewGroup al que se añadirá la nueva vista
     * @param viewType El tipo de vista
     * @return Un nuevo UserViewHolder que contiene la vista del elemento de usuario
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout del item de usuario
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view); // Retornar un nuevo UserViewHolder
    }

    /**
     * Método llamado para mostrar los datos en un ViewHolder específico.
     * @param holder El ViewHolder que debe ser actualizado para representar los contenidos del ítem en la posición dada en el conjunto de datos
     * @param position La posición del ítem dentro del conjunto de datos del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position); // Obtener el usuario en la posición específica
        // Establecer los datos del usuario en los elementos de la vista del ViewHolder
        holder.editTextEmail.setText(user.getEmail());
        holder.editTextPhone.setText(user.getPhone());
        holder.editTextUsername.setText(user.getUsername());
        holder.editTextTimeLeft.setText(String.valueOf(user.getTimeLeft() / 3600)); // Mostrar el tiempo restante en horas
        holder.checkBoxAdmin.setChecked(user.isAdmin());

        // Configurar el evento onClickListener para el botón de guardar cambios
        holder.btnSave.setOnClickListener(v -> {
            // Obtener los nuevos valores ingresados por el usuario desde los EditText y CheckBox
            String enteredEmail = holder.editTextEmail.getText().toString().trim();
            String enteredPhone = holder.editTextPhone.getText().toString().trim();
            String enteredUsername = holder.editTextUsername.getText().toString().trim();
            String enteredTimeLeft = holder.editTextTimeLeft.getText().toString().trim();
            boolean enteredIsAdmin = holder.checkBoxAdmin.isChecked();

            // Validar y actualizar los datos del usuario
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
                long timeLeftInSeconds = timeLeftInHours * 3600; // Convertir horas a segundos
                user.setTimeLeft(timeLeftInSeconds);
            }
            user.setAdmin(enteredIsAdmin);

            // Actualizar los datos del usuario en Firestore
            db.collection("users").document(user.getId())
                    .update("email", user.getEmail(),
                            "username", user.getUsername(),
                            "phone", user.getPhone(),
                            "timeLeft", user.getTimeLeft(),
                            "admin", user.isAdmin())
                    .addOnSuccessListener(aVoid -> {
                        // Mostrar mensaje de éxito si la actualización fue exitosa
                        Toast.makeText(holder.itemView.getContext(), "Cambios guardados para " + user.getUsername(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Mostrar mensaje de error si hubo un problema al actualizar
                        Toast.makeText(holder.itemView.getContext(), "Error al guardar cambios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    /**
     * Método para obtener el número total de ítems en los datos.
     * @return El número total de usuarios en la lista
     */
    @Override
    public int getItemCount() {
        return userList.size(); // Retornar el tamaño de la lista de usuarios
    }

    /**
     * Clase interna que representa el ViewHolder para un elemento de usuario.
     * Contiene referencias a las vistas de la interfaz de usuario para ese elemento.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        EditText editTextEmail, editTextPhone, editTextUsername, editTextTimeLeft; // Campos de texto para email, teléfono, nombre de usuario y tiempo restante
        CheckBox checkBoxAdmin; // CheckBox para indicar si el usuario es administrador
        Button btnSave; // Botón para guardar los cambios

        /**
         * Constructor del ViewHolder.
         * @param itemView La vista inflada que representa un elemento de usuario
         */
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar las referencias a las vistas dentro del itemView
            editTextEmail = itemView.findViewById(R.id.editTextEmail);
            editTextPhone = itemView.findViewById(R.id.editTextPhone);
            editTextUsername = itemView.findViewById(R.id.editTextUsername);
            editTextTimeLeft = itemView.findViewById(R.id.editTextTimeLeft);
            checkBoxAdmin = itemView.findViewById(R.id.checkBoxAdmin);
            btnSave = itemView.findViewById(R.id.buttonSaveUser);
        }
    }
}
