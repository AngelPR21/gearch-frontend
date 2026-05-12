package com.example.gearch_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.TallerRecienteAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Taller;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla de busqueda de talleres por nombre
public class BuscarActivity extends AppCompatActivity {

    private EditText etBuscar;
    private Button btnBuscarTaller;
    private RecyclerView rvResultados;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        etBuscar = findViewById(R.id.etBuscar);
        btnBuscarTaller = findViewById(R.id.btnBuscarTaller);
        rvResultados = findViewById(R.id.rvResultados);

        api = ApiClient.getClient().create(ApiService.class);

        // Botones de navegacion inferior
        ImageButton ibHome = findViewById(R.id.btnHome);
        ImageButton ibCitas = findViewById(R.id.btnCitas);
        ImageButton ibVehiculos = findViewById(R.id.btnVehiculos);
        ImageButton ibUsuario = findViewById(R.id.btnUsuario);

        ibHome.setOnClickListener(v -> startActivity(new Intent(this, MainClienteActivity.class)));
        ibCitas.setOnClickListener(v -> startActivity(new Intent(this, MisCitasActivity.class)));
        ibVehiculos.setOnClickListener(v -> startActivity(new Intent(this, MisVehiculosActivity.class)));
        ibUsuario.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));

        btnBuscarTaller.setOnClickListener(v -> buscar());
    }

    private void buscar() {
        String nombre = etBuscar.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Escribe el nombre del taller", Toast.LENGTH_SHORT).show();
            return;
        }

        api.buscarTalleres(nombre).enqueue(new Callback<List<Taller>>() {
            @Override
            public void onResponse(Call<List<Taller>> call, Response<List<Taller>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        Toast.makeText(BuscarActivity.this, "No se encontraron talleres", Toast.LENGTH_SHORT).show();
                    }
                    TallerRecienteAdapter adapter = new TallerRecienteAdapter(BuscarActivity.this, response.body(), v -> {
                        TallerRecienteAdapter.ViewHolder vh = (TallerRecienteAdapter.ViewHolder) rvResultados.findContainingViewHolder(v);
                        if (vh != null) {
                            Intent intent = new Intent(BuscarActivity.this, DetalleTallerActivity.class);
                            intent.putExtra("tallerId", vh.getTaller().getId());
                            startActivity(intent);
                        }
                    });
                    rvResultados.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Taller>> call, Throwable t) {
                Toast.makeText(BuscarActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
