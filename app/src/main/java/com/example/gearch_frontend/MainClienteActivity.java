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
        // FusedLocationProviderClient es el cliente de Google Play Services para obtener la ubicacion del dispositivo
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

        pedirUbicacionYCargarCercanos();
    }

    // Se ejecuta cada vez que el usuario vuelve a esta pantalla
    // Recargamos los talleres recientes para que aparezcan los nuevos tras una reserva
    @Override
    protected void onResume() {
        super.onResume();
        cargarTalleresRecientes();
    }

    // Carga los talleres en los que el usuario ha tenido citas anteriores
    private void cargarTalleresRecientes() {
        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        long usuarioId = prefs.getLong("id", -1);

        api.getCitasUsuario(usuarioId).enqueue(new Callback<List<Cita>>() {
            @Override
            public void onResponse(Call<List<Cita>> call, Response<List<Cita>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Extraemos los talleres de las citas sin repetir comparando por id
                    List<Taller> talleresRecientes = new ArrayList<>();
                    for (Cita cita : response.body()) {
                        if (cita.getTaller() != null) {
                            boolean yaEsta = false;
                            for (Taller t : talleresRecientes) {
                                if (t.getId().equals(cita.getTaller().getId())) {
                                    yaEsta = true;
                                    break;
                                }
                            }
                            if (!yaEsta) {
                                talleresRecientes.add(cita.getTaller());
                            }
                        }
                    }
                    //se crea el adapter pasandole el contexto la lista y el onclick listener
                    adapterRecientes = new TallerRecienteAdapter(MainClienteActivity.this, talleresRecientes, v -> {
                        //busca el ViewHolder de la card que se ha pulsado (v)
                        //Se castea a TallerRecienteAdapter para poder tener el metodo getTaller()
                        TallerRecienteAdapter.ViewHolder vh = (TallerRecienteAdapter.ViewHolder) rvRecientes.findContainingViewHolder(v);
                        if (vh != null) {
                            //Se crea un intent con el contexto y la actividad nueva
                            Intent intent = new Intent(MainClienteActivity.this, DetalleTallerActivity.class);
                            //se le añade la id del taller para que pueda mostrar la info de ese taller, lo hacemos de esta forma para
                            // que asi la info siempre este actualizada, si no al pasarle el objeto podria tener datos anteriores
                            intent.putExtra("tallerId", vh.getTaller().getId());
                            startActivity(intent);
                        }
                    });
                    //se asigna el adapter al rv
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
        // checkSelfPermission comprueba si el usuario ya concedio el permiso anteriormente
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no tiene permiso mostramos el dialogo del sistema para pedirlo
            // PERMISO_UBICACION es el codigo que usaremos en onRequestPermissionsResult
            // para identificar que este es el permiso que se esta respondiendo
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_UBICACION);
        } else {
            // Si ya tiene permiso obtenemos la ubicacion directamente
            obtenerUbicacionYCargar();
        }
    }

    // Se ejecuta automaticamente cuando el usuario responde al dialogo de permiso
    // requestCode identifica de que permiso viene la respuesta (puede haber varios permisos pedidos)
    // grantResults contiene si el usuario acepto o denego cada permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_UBICACION && grantResults.length > 0 //mayor que 0 porque android devuelve una lista y podria fallar si se cierra el texto de pedir permisos
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//posicion 0 porque ahi esta el permiso que queremos
            // El usuario acepto el permiso, obtenemos la ubicacion
            obtenerUbicacionYCargar();
        } else {
            // El usuario denego el permiso, no podemos mostrar talleres cercanos
            Toast.makeText(this, "Permiso de ubicacion denegado", Toast.LENGTH_SHORT).show();
        }
    }

    // Obtiene la ubicacion actual del dispositivo y carga los talleres cercanos
    // En el emulador puede devolver null porque no tiene GPS real
    private void obtenerUbicacionYCargar() {
        // Doble comprobacion del permiso requerida por Android antes de llamar a getCurrentLocation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        // PRIORITY_BALANCED_POWER_ACCURACY usa GPS + WiFi + red movil para obtener la ubicacion
        // Es menos preciso que PRIORITY_HIGH_ACCURACY pero consume menos bateria
        // El segundo parametro null es un CancellationToken para cancelar la peticion, no lo necesitamos
        locationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(ubicacion -> {
                    if (ubicacion != null) {
                        // Enviamos las coordenadas al backend para buscar talleres en un radio de 100km
                        cargarTalleresCercanos(ubicacion.getLatitude(), ubicacion.getLongitude());
                    } else {
                        // Puede ocurrir en el emulador al no tener GPS real configurado
                        Toast.makeText(MainClienteActivity.this, "No se pudo obtener la ubicacion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Llama al backend con las coordenadas y muestra los talleres en un radio de 10km
    // (He puesto 100 para hacer pruebas pero lo normal seria 10)
    private void cargarTalleresCercanos(double lat, double lng) {
        api.getTalleresCercanos(lat, lng, 100).enqueue(new Callback<List<Taller>>() {
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
