package com.example.gearch_frontend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.ResenaAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Resena;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla del admin para ver las resenas del taller
// Muestra la puntuacion media y la lista completa de resenas
public class ResenasAdminActivity extends AppCompatActivity {

    private TextView tvEstadisticas;
    private RecyclerView rvResenas;
    private ApiService api;
    private long tallerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resenas_admin);

        // Boton de volver atras
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        tvEstadisticas = findViewById(R.id.tvEstadisticas);
        rvResenas = findViewById(R.id.rvResenas);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        tallerId = prefs.getLong("tallerId", -1);

        cargarResenas();
    }

    private void cargarResenas() {
        api.getResenasByTaller(tallerId).enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Resena> resenas = response.body();

                    // Calculamos la media de puntuacion
                    if (resenas.isEmpty()) {
                        tvEstadisticas.setText("Puntuacion media: sin resenas todavia");
                    } else {
                        double suma = 0;
                        for (Resena r : resenas) {
                            suma += r.getPuntuacion();
                        }
                        double media = suma / resenas.size();
                        tvEstadisticas.setText(String.format("Puntuacion media: %.1f (%d resenas)", media, resenas.size()));
                    }

                    // Reutilizamos el ResenaAdapter que ya existe en el proyecto
                    ResenaAdapter adapter = new ResenaAdapter(ResenasAdminActivity.this, resenas);
                    rvResenas.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                Toast.makeText(ResenasAdminActivity.this, "Error al cargar las resenas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
