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

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Cita;
import com.example.gearch_frontend.api.models.Taller;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainClienteActivity extends AppCompatActivity {

    private RecyclerView rvRecientes, rvCercanos;
    private TallerAdapter adapterRecientes, adapterCercanos;

    // Cliente de Google para obtener la ubicacion del dispositivo
    private FusedLocationProviderClient locationClient  ;
    private ApiService api;

    // Codigo identificador para el permiso de ubicacion
    // Se usa en onRequestPermissionsResult para saber que permiso se esta pidiendo
    private static final int PERMISO_UBICACION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cliente);

        rvRecientes = findViewById(R.id.rvRecientes);
        rvCercanos = findViewById(R.id.rvCercanos);

        // Recyclerview de recientes horizontal
        rvRecientes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // RecyclerView de cercanos vertical
        rvCercanos.setLayoutManager(new LinearLayoutManager(this));

        // Creamos la conexion con el backend
        api = ApiClient.getClient().create(ApiService.class);
        // Creamos el cliente de ubicacion de Google
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        // Botones de navegación inferior
        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnCitas = findViewById(R.id.btnCitas);
        ImageButton btnBuscar = findViewById(R.id.btnBuscar);
        ImageButton btnVehiculos = findViewById(R.id.btnVehiculos);
        ImageButton btnUsuario = findViewById(R.id.btnUsuario);

        // Al pulsar cada boton abre la Activity correspondiente
        btnCitas.setOnClickListener(v -> startActivity(new Intent(this, MisCitasActivity.class)));
        btnBuscar.setOnClickListener(v -> startActivity(new Intent(this, BuscarActivity.class)));
        btnVehiculos.setOnClickListener(v -> startActivity(new Intent(this, MisVehiculosActivity.class)));

        // Cargamos los datos al entrar a la pantalla
        cargarTalleresRecientes();
        pedirUbicacionYCargarCercanos();
    }

    // Carga los talleres en los que el usuario ha tenido citas anteriores
    private void cargarTalleresRecientes() {
        // Obtenemos el id del usuario guardado en SharedPreferences al hacer login
        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        Long usuarioId = prefs.getLong("id", -1);

        // Llamada al backend para obtener las citas del usuario
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

                    // Creamos el adapter con los talleres y el listener de click
                    adapterRecientes = new TallerAdapter(MainClienteActivity.this, talleresRecientes, v -> {
                        // Al pulsar un taller buscamos el ViewHolder que contiene la vista pulsada
                        TallerAdapter.ViewHolder vh = (TallerAdapter.ViewHolder) rvRecientes.findContainingViewHolder(v);
                        if (vh != null) {
                            // Abrimos el detalle del taller pasandole su id
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
            // No tiene permiso, mostramos el dialogo al usuario para pedirlo
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_UBICACION);
        } else {
            // Ya tiene permiso, obtenemos la ubicacion directamente
            obtenerUbicacionYCargar();
        }
    }

    // Se ejecuta automaticamente cuando el usuario responde al dialogo de permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_UBICACION && grantResults.length > 0 //si llama y es 0 peta
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //la posicion 1 del array de permisos tiene que tener el que hemos pedido
            // El usuario acepto el permiso, obtenemos la ubicacion
            obtenerUbicacionYCargar();
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }

    // Obtiene la ultima ubicacion conocida del dispositivo
    private void obtenerUbicacionYCargar() {
        //getLastLocation obliga a comprobar que tenemos el permiso
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        // getLastLocation devuelve la ultima ubicacion conocida del GPS
        locationClient.getLastLocation().addOnSuccessListener(ubicacion -> {
            if (ubicacion != null) {
                // Tenemos ubicacion, cargamos los talleres cercanos
                cargarTalleresCercanos(ubicacion.getLatitude(), ubicacion.getLongitude());
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Llama al backend con las coordenadas y muestra los talleres en un radio de 10km
    private void cargarTalleresCercanos(double lat, double lng) {
        //radio 10 son 10 km
        api.getTalleresCercanos(lat, lng, 10).enqueue(new Callback<List<Taller>>() {
            @Override
            public void onResponse(Call<List<Taller>> call, Response<List<Taller>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Creamos el adapter con los talleres cercanos y el listener de click
                    adapterCercanos = new TallerAdapter(MainClienteActivity.this, response.body(), v -> {//este lamba acorta el onclicklistener directamente
                        // Al pulsar un taller buscamos el ViewHolder que contiene la vista pulsada
                        TallerAdapter.ViewHolder vh = (TallerAdapter.ViewHolder) rvCercanos.findContainingViewHolder(v);
                        if (vh != null) {
                            // Abrimos el detalle del taller pasandole su id
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