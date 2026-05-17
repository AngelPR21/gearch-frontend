package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.CitaAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Cita;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla que muestra todas las citas del usuario filtradas por estado
public class MisCitasActivity extends AppCompatActivity {

    private RecyclerView rvCitas;
    private Spinner spinnerEstado;
    private ApiService api;

    // Guardamos todas las citas para filtrar sin volver a llamar al backend
    private List<Cita> todasLasCitas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_citas);

        rvCitas = findViewById(R.id.rvCitas);
        spinnerEstado = findViewById(R.id.spinnerEstado);

        // Botones de navegacion inferior
        ImageButton ibHome = findViewById(R.id.btnHome);
        ImageButton ibBuscar = findViewById(R.id.btnBuscar);
        ImageButton ibVehiculos = findViewById(R.id.btnVehiculos);
        ImageButton ibUsuario = findViewById(R.id.btnUsuario);

        ibHome.setOnClickListener(v -> startActivity(new Intent(this, MainClienteActivity.class)));
        ibBuscar.setOnClickListener(v -> startActivity(new Intent(this, BuscarActivity.class)));
        ibVehiculos.setOnClickListener(v -> startActivity(new Intent(this, MisVehiculosActivity.class)));
        ibUsuario.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        long usuarioId = prefs.getLong("id", -1);

        configurarSpinner();
        cargarCitas(usuarioId);
    }

    private void configurarSpinner() {
        List<String> opciones = new ArrayList<>();
        opciones.add("Todas");
        opciones.add("CONFIRMADA");
        opciones.add("CANCELADA");
        opciones.add("COMPLETADA");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);

        // Al cambiar el filtro refrescamos la lista sin volver a llamar al backend
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                filtrarCitas(opciones.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Filtra la lista local segun el estado seleccionado en el Spinner
    private void filtrarCitas(String filtro) {
        List<Cita> citasFiltradas = new ArrayList<>();

        if (filtro.equals("Todas")) {
            citasFiltradas = todasLasCitas;
        } else {
            for (Cita c : todasLasCitas) {
                if (c.getEstado().toString().equals(filtro)) {
                    citasFiltradas.add(c);
                }
            }
        }

        CitaAdapter adapter = new CitaAdapter(MisCitasActivity.this, citasFiltradas);
        rvCitas.setAdapter(adapter);
    }

    // Carga todas las citas del usuario y aplica el filtro actual del Spinner
    private void cargarCitas(long usuarioId) {
        api.getCitasUsuario(usuarioId).enqueue(new Callback<List<Cita>>() {
            @Override
            public void onResponse(Call<List<Cita>> call, Response<List<Cita>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todasLasCitas = response.body();
                    // Aplicamos el filtro actual del Spinner
                    filtrarCitas(spinnerEstado.getSelectedItem().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Cita>> call, Throwable t) {
                Toast.makeText(MisCitasActivity.this, "Error al cargar las citas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}