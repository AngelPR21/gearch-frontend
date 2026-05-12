package com.example.gearch_frontend;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.TallerCercanoAdapter;
import com.example.gearch_frontend.adapters.TallerRecienteAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Cita;
import com.example.gearch_frontend.api.models.Taller;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla principal del cliente
// Muestra dos RecyclerViews: talleres recientes (horizontal) y talleres cercanos (vertical)
// Contiene la barra de navegacion inferior con acceso a las demas pantallas
public class MainClienteActivity extends AppCompatActivity {

    private RecyclerView rvRecientes, rvCercanos;
    private TallerRecienteAdapter adapterRecientes;
    private TallerCercanoAdapter adapterCercanos;

    // Cliente de Google para obtener la ubicacion del dispositivo
    private FusedLocationProviderClient locationClient;
    private ApiService api;

    // Codigo identificador para el permiso de ubicacion
    // Se usa en onRequestPermissionsResult para saber que permiso se esta respondiendo
    private static final int PERMISO_UBICACION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cliente);

        rvRecientes = findViewById(R.id.rvRecientes);
        rvCercanos = findViewById(R.id.rvCercanos);

        // RecyclerView de recientes horizontal
        rvRecientes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // RecyclerView de cercanos vertical
        rvCercanos.setLayoutManager(new LinearLayoutManager(this));

        api = ApiClient.getClient().create(ApiService.class);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        // Botones de navegacion inferior
        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnCitas = findViewById(R.id.btnCitas);
        ImageButton btnBuscar = findViewById(R.id.btnBuscar);
        ImageButton btnVehiculos = findViewById(R.id.btnVehiculos);
        ImageButton btnUsuario = findViewById(R.id.btnUsuario);

        btnCitas.setOnClickListener(v -> startActivity(new Intent(this, MisCitasActivity.class)));
        btnBuscar.setOnClickListener(v -> startActivity(new Intent(this, BuscarActivity.class)));
        btnVehiculos.setOnClickListener(v -> startActivity(new Intent(this, MisVehiculosActivity.class)));
        btnUsuario.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));

        cargarTalleresRecientes();
        pedirUbicacionYCargarCercanos();
    }

    // Carga los talleres en los que el usuario ha tenido citas anteriores
    private void cargarTalleresRecientes() {
        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        Long usuarioId = prefs.getLong("id", -1);

        api.getCitasUsuario(usuarioId).enqueue(new Callback<List<Cita>>() {
            @Override
            public void onResponse(Call<List<Cita>> call, Response<List<Cita>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Extraemos los talleres de las citas sin repetir
                    // Si el usuario tiene 3 citas en el mismo taller, solo aparece una vez
                    List<Taller> talleresRecientes = new ArrayList<>();
                    for (Cita cita : response.body()) {
                        if (cita.getTaller() != null && !talleresRecientes.contains(cita.getTaller())) {
                            talleresRecientes.add(cita.getTaller());
                        }
                    }

                    adapterRecientes = new TallerRecienteAdapter(MainClienteActivity.this, talleresRecientes, v -> {
                        TallerRecienteAdapter.ViewHolder vh = (TallerRecienteAdapter.ViewHolder) rvRecientes.findContainingViewHolder(v);
                        if (vh != null) {
                            Intent intent = new Intent(MainClienteActivity.this, DetalleTallerActivity.class);
                            intent.putExtra("tallerId", vh.getTaller().getId());
                            startActivity(intent);
                        }
                    });
                    rvRecientes.setAdapter(adapterRecientes);
                }
            }

            @Override
            public void onFailure(Call<List<Cita>> call, Throwable t) {
                Toast.makeText(MainClienteActivity.this, "Error al cargar talleres recientes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Comprueba si tiene permiso de ubicacion y lo pide si no lo tiene
    private void pedirUbicacionYCargarCercanos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_UBICACION);
        } else {
            obtenerUbicacionYCargar();
        }
    }

    // Se ejecuta automaticamente cuando el usuario responde al dialogo de permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_UBICACION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionYCargar();
        } else {
            Toast.makeText(this, "Permiso de ubicacion denegado", Toast.LENGTH_SHORT).show();
        }
    }

    // Obtiene la ultima ubicacion conocida del dispositivo
    // En el emulador puede devolver null porque no tiene GPS real
    private void obtenerUbicacionYCargar() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        locationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(ubicacion -> {
                    if (ubicacion != null) {
                        cargarTalleresCercanos(ubicacion.getLatitude(), ubicacion.getLongitude());
                    } else {
                        Toast.makeText(MainClienteActivity.this, "No se pudo obtener la ubicacion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Llama al backend con las coordenadas y muestra los talleres en un radio de 10km
    private void cargarTalleresCercanos(double lat, double lng) {
        api.getTalleresCercanos(lat, lng, 10).enqueue(new Callback<List<Taller>>() {
            @Override
            public void onResponse(Call<List<Taller>> call, Response<List<Taller>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapterCercanos = new TallerCercanoAdapter(MainClienteActivity.this, response.body(), v -> {
                        TallerCercanoAdapter.ViewHolder vh = (TallerCercanoAdapter.ViewHolder) rvCercanos.findContainingViewHolder(v);
                        if (vh != null) {
                            Intent intent = new Intent(MainClienteActivity.this, DetalleTallerActivity.class);
                            intent.putExtra("tallerId", vh.getTaller().getId());
                            startActivity(intent);
                        }
                    });
                    rvCercanos.setAdapter(adapterCercanos);
                }
            }

            @Override
            public void onFailure(Call<List<Taller>> call, Throwable t) {
                Toast.makeText(MainClienteActivity.this, "Error al cargar talleres cercanos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}