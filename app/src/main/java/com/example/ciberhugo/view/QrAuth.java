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

        btnQr = findViewById(R.id.scanButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        firebaseDBConnection = new FirebaseDBConnection();

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        if (userId != null) {
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String uniqueContent = userId + "_" + timestamp;

            generateQrCode(uniqueContent);
        }

        btnQr.setOnClickListener(v -> {
            // Añadir log a Firestore
            firebaseDBConnection.insertLog(userId, "Inicio de sesión", "Usuario ha escaneado en el local");
            Intent intentQr = new Intent(QrAuth.this, UserActivity.class);
            intentQr.putExtra("email", userId);
            startActivity(intentQr);
        });
    }

    private void generateQrCode(String content) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 283, 278);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
