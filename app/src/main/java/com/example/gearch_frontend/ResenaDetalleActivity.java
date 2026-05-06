package com.example.gearch_frontend;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResenaDetalleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resena_detalle);

        TextView tvPuntuacion = findViewById(R.id.tvPuntuacion);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvComentario = findViewById(R.id.tvComentario);

        // Recibimos los datos de la reseña
        tvPuntuacion.setText("⭐ " + getIntent().getIntExtra("puntuacion", 0) + "/5");
        tvFecha.setText(getIntent().getStringExtra("fecha"));
        tvComentario.setText(getIntent().getStringExtra("comentario"));
    }
}