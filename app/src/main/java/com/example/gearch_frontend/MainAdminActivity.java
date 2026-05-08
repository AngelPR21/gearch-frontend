package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Resena;
import com.example.gearch_frontend.api.models.Taller;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainAdminActivity extends AppCompatActivity {

    private TextView tvNombreTaller, tvPuntuacion;
    private Button btnCitas, btnServicios, btnHorario, btnResenas, btnEditarTaller, btnCerrarSesion;
    private ApiService api;
    private Long tallerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        tvNombreTaller = findViewById(R.id.tvNombreTaller);
        tvPuntuacion = findViewById(R.id.tvPuntuacion);
        btnCitas = findViewById(R.id.btnCitas);
        btnServicios = findViewById(R.id.btnServicios);
        btnHorario = findViewById(R.id.btnHorario);
        btnResenas = findViewById(R.id.btnResenas);
        btnEditarTaller = findViewById(R.id.btnEditarTaller);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        tallerId = prefs.getLong("tallerId", -1);

        cargarTaller();
        cargarPuntuacion();

        btnCitas.setOnClickListener(v -> startActivity(new Intent(this, CitasAdminActivity.class)));
        btnServicios.setOnClickListener(v -> startActivity(new Intent(this, ServiciosAdminActivity.class)));
        btnHorario.setOnClickListener(v -> startActivity(new Intent(this, HorarioAdminActivity.class)));
        btnResenas.setOnClickListener(v -> startActivity(new Intent(this, ResenasAdminActivity.class)));
        btnEditarTaller.setOnClickListener(v -> startActivity(new Intent(this, EditarTallerActivity.class)));

        btnCerrarSesion.setOnClickListener(v -> {
            // Limpiamos la sesión y volvemos al login
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    // Carga el nombre del taller
    private void cargarTaller() {
        api.getTallerById(tallerId).enqueue(new Callback<Taller>() {
            @Override
            public void onResponse(Call<Taller> call, Response<Taller> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvNombreTaller.setText(response.body().getNombre());
                }
            }

            @Override
            public void onFailure(Call<Taller> call, Throwable t) {
                Toast.makeText(MainAdminActivity.this, "Error al cargar el taller", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Carga las reseñas y calcula la media de puntuación
    private void cargarPuntuacion() {
        api.getResenasByTaller(tallerId).enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Resena> resenas = response.body();
                    int total = resenas.size();

                    if (total == 0) {
                        tvPuntuacion.setText("Sin reseñas todavía");
                        return;
                    }

                    // Calculamos la media sumando todas las puntuaciones y dividiendo
                    double suma = 0;
                    for (Resena r : resenas) {
                        suma += r.getPuntuacion();
                    }
                    double media = suma / total;

                    tvPuntuacion.setText(String.format("%.1f (%d reseñas)", media, total));
                }
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                Toast.makeText(MainAdminActivity.this, "Error al cargar las reseñas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}