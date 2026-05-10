package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.ServicioAdminAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Servicio;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiciosAdminActivity extends AppCompatActivity {

    private RecyclerView rvServicios;
    private Button btnAnadir;
    private ApiService api;
    private Long tallerId;
    private Long adminId;

    ActivityResultLauncher<Intent> anadirServicioLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    cargarServicios();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios_admin);

        rvServicios = findViewById(R.id.rvServicios);
        btnAnadir = findViewById(R.id.btnAnadir);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        tallerId = prefs.getLong("tallerId", -1);
        adminId = prefs.getLong("id", -1);

        cargarServicios();

        btnAnadir.setOnClickListener(v -> {
            anadirServicioLauncher.launch(new Intent(this, AnadirServicioActivity.class));
        });
    }

    private void cargarServicios() {
        api.getServiciosByTaller(tallerId).enqueue(new Callback<List<Servicio>>() {
            @Override
            public void onResponse(Call<List<Servicio>> call, Response<List<Servicio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ServicioAdminAdapter adapter = new ServicioAdminAdapter(
                            ServiciosAdminActivity.this,
                            response.body(),
                            v -> {
                                ServicioAdminAdapter.ViewHolder vh = (ServicioAdminAdapter.ViewHolder) rvServicios.findContainingViewHolder(v);
                                if (vh != null) confirmarEliminar(vh.getServicio());
                            });
                    rvServicios.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Servicio>> call, Throwable t) {
                Toast.makeText(ServiciosAdminActivity.this, "Error al cargar los servicios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarEliminar(Servicio servicio) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar servicio")
                .setMessage("¿Estás seguro de que quieres eliminar " + servicio.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarServicio(servicio))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarServicio(Servicio servicio) {
        api.eliminarServicio(adminId, servicio.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ServiciosAdminActivity.this, "Servicio eliminado", Toast.LENGTH_SHORT).show();
                    cargarServicios();
                } else {
                    Toast.makeText(ServiciosAdminActivity.this, "Error al eliminar el servicio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ServiciosAdminActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}