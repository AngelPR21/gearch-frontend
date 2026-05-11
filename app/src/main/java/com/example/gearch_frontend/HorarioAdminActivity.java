package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.HorarioAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.DisponibilidadTaller;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla del admin para gestionar el horario semanal del taller
// Permite ver los dias configurados, anadir nuevos dias y eliminar los existentes
public class HorarioAdminActivity extends AppCompatActivity {

    private RecyclerView rvHorario;
    private Button btnAnadir;
    private ApiService api;
    private Long adminId;

    // Launcher para abrir AnadirHorarioActivity y recargar el horario al volver
    ActivityResultLauncher<Intent> anadirHorarioLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    cargarHorario();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_admin);

        rvHorario = findViewById(R.id.rvHorario);
        btnAnadir = findViewById(R.id.btnAnadir);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        adminId = prefs.getLong("id", -1);

        cargarHorario();

        btnAnadir.setOnClickListener(v -> {
            anadirHorarioLauncher.launch(new Intent(this, AnadirHorarioActivity.class));
        });
    }

    private void cargarHorario() {
        api.getHorario(adminId).enqueue(new Callback<List<DisponibilidadTaller>>() {
            @Override
            public void onResponse(Call<List<DisponibilidadTaller>> call, Response<List<DisponibilidadTaller>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HorarioAdapter adapter = new HorarioAdapter(
                            HorarioAdminActivity.this,
                            response.body(),
                            v -> {
                                HorarioAdapter.ViewHolder vh = (HorarioAdapter.ViewHolder) rvHorario.findContainingViewHolder(v);
                                if (vh != null) eliminarHorario(vh.getHorario());
                            });
                    rvHorario.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<DisponibilidadTaller>> call, Throwable t) {
                Toast.makeText(HorarioAdminActivity.this, "Error al cargar el horario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarHorario(DisponibilidadTaller horario) {
        api.eliminarHorario(adminId, horario.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(HorarioAdminActivity.this, "Dia eliminado", Toast.LENGTH_SHORT).show();
                    cargarHorario();
                } else {
                    Toast.makeText(HorarioAdminActivity.this, "Error al eliminar el dia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(HorarioAdminActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
