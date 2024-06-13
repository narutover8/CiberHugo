/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 * QrAuth permite generar un código QR único para autenticación del usuario basado en su ID. El código QR se genera
 * al recibir el ID de usuario como un extra del intent. Además, registra la acción de inicio de sesión en Firestore
 * cuando el usuario escanea el código QR y redirige al usuario a la actividad principal.
 */

package com.example.ciberhugo.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ciberhugo.controller.FirebaseDBConnection;
import com.example.ciberhugo.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QrAuth extends AppCompatActivity {

    private Button btnQr;
    private ImageView qrCodeImageView;
    private String userId;
    private FirebaseDBConnection firebaseDBConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_authentication);

        // Inicializar componentes de la interfaz
        btnQr = findViewById(R.id.scanButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        // Inicializar FirebaseDBConnection
        firebaseDBConnection = new FirebaseDBConnection();

        // Obtener el ID de usuario del intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        // Generar código QR si el ID de usuario no es nulo
        if (userId != null) {
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String uniqueContent = userId + "_" + timestamp;

            generateQrCode(uniqueContent); // Generar código QR único
        }

        // Configurar listener para el botón de escaneo QR
        btnQr.setOnClickListener(v -> {
            // Registrar el inicio de sesión en Firestore
            firebaseDBConnection.insertLog(userId, "Inicio de sesión", "Usuario ha escaneado en el local");

            // Redirigir a la actividad principal del usuario
            Intent intentQr = new Intent(QrAuth.this, UserActivity.class);
            intentQr.putExtra("email", userId); // Pasar el ID de usuario como extra
            startActivity(intentQr);
        });
    }

    /**
     * Genera un código QR basado en el contenido proporcionado y lo muestra en un ImageView.
     *
     * @param content El contenido que se codificará en el código QR.
     */    private void generateQrCode(String content) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 283, 278); // Dimensiones del código QR
            qrCodeImageView.setImageBitmap(bitmap); // Mostrar el código QR generado en ImageView
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
