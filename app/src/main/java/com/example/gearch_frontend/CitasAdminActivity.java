package com.example.gearch_frontend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.CitaAdminAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Cita;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla del admin para gestionar las citas del taller
// Permite filtrar por estado y cancelar o completar cada cita
public class CitasAdminActivity extends AppCompatActivity {

    private Spinner spinnerEstado;
    private RecyclerView rvCitas;
    private ApiService api;
    private Long tallerId;

    // Guardamos todas las citas para poder filtrar sin volver a llamar al backend
    private List<Cita> todasLasCitas = new ArrayList<>();
    private CitaAdminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas_admin);

        spinnerEstado = findViewById(R.id.spinnerEstado);
        rvCitas = findViewById(R.id.rvCitas);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        tallerId = prefs.getLong("tallerId", -1);

        configurarSpinner();
        cargarCitas();
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

        adapter = new CitaAdminAdapter(this, citasFiltradas,
                v -> {
                    CitaAdminAdapter.ViewHolder vh = (CitaAdminAdapter.ViewHolder) rvCitas.findContainingViewHolder(v);
                    if (vh != null) cambiarEstado(vh.getCita(), "CANCELADA");
                },
                v -> {
                    CitaAdminAdapter.ViewHolder vh = (CitaAdminAdapter.ViewHolder) rvCitas.findContainingViewHolder(v);
                    if (vh != null) cambiarEstado(vh.getCita(), "COMPLETADA");
                });
        rvCitas.setAdapter(adapter);
    }

    private void cargarCitas() {
        api.getCitasTaller(tallerId).enqueue(new Callback<List<Cita>>() {
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
                Toast.makeText(CitasAdminActivity.this, "Error al cargar las citas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cambiarEstado(Cita cita, String estado) {
        api.actualizarEstadoCita(cita.getId(), estado).enqueue(new Callback<Cita>() {
            @Override
            public void onResponse(Call<Cita> call, Response<Cita> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CitasAdminActivity.this, "Estado actualizado", Toast.LENGTH_SHORT).show();
                    cargarCitas();
                } else {
                    Toast.makeText(CitasAdminActivity.this, "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cita> call, Throwable t) {
                Toast.makeText(CitasAdminActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
