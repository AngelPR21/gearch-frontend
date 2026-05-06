package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.ResenaAdapter;
import com.example.gearch_frontend.adapters.ServicioAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Resena;
import com.example.gearch_frontend.api.models.Servicio;
import com.example.gearch_frontend.api.models.Taller;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleTallerActivity extends AppCompatActivity {

    private TextView tvNombre, tvDireccion, tvTelefono, tvDescripcion;
    private ImageView ivFoto;
    private RecyclerView rvServicios, rvResenas;
    private Button btnReservar, btnEscribirResena;
    private ApiService api;
    private Long tallerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_taller);

        tvNombre = findViewById(R.id.tvNombreTaller);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        ivFoto = findViewById(R.id.ivFotoTaller);
        rvServicios = findViewById(R.id.rvServicios);
        rvResenas = findViewById(R.id.rvResenas);
        btnReservar = findViewById(R.id.btnReservar);
        btnEscribirResena = findViewById(R.id.btnEscribirResena);

        rvServicios.setLayoutManager(new LinearLayoutManager(this));
        rvResenas.setLayoutManager(new LinearLayoutManager(this));

        api = ApiClient.getClient().create(ApiService.class);

        // Recibimos el id del taller que nos pasó MainClienteActivity
        tallerId = getIntent().getLongExtra("tallerId", -1);

        cargarDatosTaller();
        cargarServicios();
        cargarResenas();

        // Al pulsar reservar abrimos ReservarActivity pasando el id del taller
        btnReservar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservarActivity.class);
            intent.putExtra("tallerId", tallerId);
            startActivity(intent);
        });

        // Al pulsar escribir reseña abrimos EscribirResenaActivity pasando el id del taller
        btnEscribirResena.setOnClickListener(v -> {
            Intent intent = new Intent(this, EscribirResenaActivity.class);
            intent.putExtra("tallerId", tallerId);
            startActivity(intent);
        });
    }

    // Carga los datos principales del taller
    private void cargarDatosTaller() {
        api.getTallerById(tallerId).enqueue(new Callback<Taller>() {
            @Override
            public void onResponse(Call<Taller> call, Response<Taller> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Taller taller = response.body();
                    tvNombre.setText(taller.getNombre());
                    tvDireccion.setText(taller.getDireccion());
                    tvTelefono.setText(taller.getTelefono());
                    tvDescripcion.setText(taller.getDescripcion());

                    // Si el taller no tiene foto mostramos la imagen por defecto
                    if (taller.getFotoPerfil() != null) {
                        ivFoto.setImageBitmap(android.graphics.BitmapFactory.decodeByteArray(
                                taller.getFotoPerfil(), 0, taller.getFotoPerfil().length));
                    } else {
                        ivFoto.setImageResource(R.drawable.ic_launcher_background);
                    }
                }
            }

            @Override
            public void onFailure(Call<Taller> call, Throwable t) {
                Toast.makeText(DetalleTallerActivity.this, "Error al cargar el taller", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Carga los servicios del taller
    private void cargarServicios() {
        api.getServiciosByTaller(tallerId).enqueue(new Callback<List<Servicio>>() {
            @Override
            public void onResponse(Call<List<Servicio>> call, Response<List<Servicio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ServicioAdapter adapter = new ServicioAdapter(DetalleTallerActivity.this, response.body());
                    rvServicios.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Servicio>> call, Throwable t) {
                Toast.makeText(DetalleTallerActivity.this, "Error al cargar servicios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Carga las reseñas del taller
    private void cargarResenas() {
        api.getResenasByTaller(tallerId).enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResenaAdapter adapter = new ResenaAdapter(DetalleTallerActivity.this, response.body());
                    rvResenas.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                Toast.makeText(DetalleTallerActivity.this, "Error al cargar reseñas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}