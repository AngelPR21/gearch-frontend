package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.CitaAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Cita;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisCitasActivity extends AppCompatActivity {

    private RecyclerView rvCitas;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_citas);

        rvCitas = findViewById(R.id.rvCitas);
        ImageButton ibHome = findViewById(R.id.btnHome);
        ImageButton ibCitas = findViewById(R.id.btnCitas);
        ImageButton ibBuscar = findViewById(R.id.btnBuscar);
        ImageButton ibVehiculos = findViewById(R.id.btnVehiculos);
        ImageButton ibUsuario = findViewById(R.id.btnUsuario);

        ibHome.setOnClickListener(v -> startActivity(new Intent(this, MainClienteActivity.class)));
        ibBuscar.setOnClickListener(v -> startActivity(new Intent(this, BuscarActivity.class)));
        ibVehiculos.setOnClickListener(v -> startActivity(new Intent(this, MisVehiculosActivity.class)));

        api = ApiClient.getClient().create(ApiService.class);

        // Obtenemos el id del usuario guardado en SharedPreferences al hacer login
        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        Long usuarioId = prefs.getLong("id", -1);

        cargarCitas(usuarioId);

    }

    // Carga las citas del usuario y las muestra en el RecyclerView
    private void cargarCitas(Long usuarioId) {
        api.getCitasUsuario(usuarioId).enqueue(new Callback<List<Cita>>() {
            @Override
            public void onResponse(Call<List<Cita>> call, Response<List<Cita>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CitaAdapter adapter = new CitaAdapter(MisCitasActivity.this, response.body());
                    rvCitas.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Cita>> call, Throwable t) {
                Toast.makeText(MisCitasActivity.this, "Error al cargar las citas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}