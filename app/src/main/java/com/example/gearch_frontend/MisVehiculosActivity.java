package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.VehiculoAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Vehiculo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla que muestra los vehiculos del usuario
// Permite anadir nuevos vehiculos y eliminar los existentes
public class MisVehiculosActivity extends AppCompatActivity {

    private RecyclerView rvVehiculos;
    private Button btnAnadir;
    private VehiculoAdapter adapter;
    private ApiService api;
    private long usuarioId;

    // Launcher para abrir AnadirVehiculoActivity y recargar la lista al volver
    // registerForActivityResult es el reemplazo moderno de startActivityForResult
    ActivityResultLauncher<Intent> anadirVehiculoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    cargarVehiculos();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_vehiculos);

        rvVehiculos = findViewById(R.id.rvVehiculos);
        btnAnadir = findViewById(R.id.btnAnadir);

        // Botones de navegacion inferior
        ImageButton ibHome = findViewById(R.id.btnHome);
        ImageButton ibCitas = findViewById(R.id.btnCitas);
        ImageButton ibBuscar = findViewById(R.id.btnBuscar);
        ImageButton ibUsuario = findViewById(R.id.btnUsuario);

        ibHome.setOnClickListener(v -> startActivity(new Intent(this, MainClienteActivity.class)));
        ibCitas.setOnClickListener(v -> startActivity(new Intent(this, MisCitasActivity.class)));
        ibBuscar.setOnClickListener(v -> startActivity(new Intent(this, BuscarActivity.class)));
        ibUsuario.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        usuarioId = prefs.getLong("id", -1);

        cargarVehiculos();

        btnAnadir.setOnClickListener(v -> {
            anadirVehiculoLauncher.launch(new Intent(this, AnadirVehiculoActivity.class));
        });
    }

    private void cargarVehiculos() {
        api.getVehiculosUsuario(usuarioId).enqueue(new Callback<List<Vehiculo>>() {
            @Override
            public void onResponse(Call<List<Vehiculo>> call, Response<List<Vehiculo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new VehiculoAdapter(MisVehiculosActivity.this, response.body(), v -> {
                        // findContainingViewHolder busca el ViewHolder que contiene el boton pulsado
                        VehiculoAdapter.ViewHolder vh = (VehiculoAdapter.ViewHolder) rvVehiculos.findContainingViewHolder(v);
                        if (vh != null) {
                            eliminarVehiculo(vh.getVehiculo());
                        }
                    });
                    rvVehiculos.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Vehiculo>> call, Throwable t) {
                Toast.makeText(MisVehiculosActivity.this, "Error al cargar los vehiculos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarVehiculo(Vehiculo vehiculo) {
        api.eliminarVehiculo(vehiculo.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MisVehiculosActivity.this, "Vehiculo eliminado", Toast.LENGTH_SHORT).show();
                    cargarVehiculos();
                } else {
                    Toast.makeText(MisVehiculosActivity.this, "Error al eliminar el vehiculo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MisVehiculosActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
