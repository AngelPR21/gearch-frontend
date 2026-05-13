package com.example.gearch_frontend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Servicio;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla para anadir un nuevo servicio al taller del admin
// Al guardar correctamente devuelve RESULT_OK a ServiciosAdminActivity para que recargue la lista
public class AnadirServicioActivity extends AppCompatActivity {

    private EditText etNombre, etDescripcion, etPrecio;
    private Button btnGuardar;
    private ApiService api;
    private long adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_servicio);

        // Boton de volver atras
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        btnGuardar = findViewById(R.id.btnGuardar);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        adminId = prefs.getLong("id", -1);

        btnGuardar.setOnClickListener(v -> guardarServicio());
    }

    private void guardarServicio() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();

        if (nombre.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Nombre y precio son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Servicio servicio = new Servicio();
        servicio.setNombre(nombre);
        servicio.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        servicio.setPrecio(Double.parseDouble(precioStr));

        api.crearServicio(adminId, servicio).enqueue(new Callback<Servicio>() {
            @Override
            public void onResponse(Call<Servicio> call, Response<Servicio> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AnadirServicioActivity.this, "Servicio anadido correctamente", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AnadirServicioActivity.this, "Error al anadir el servicio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Servicio> call, Throwable t) {
                Toast.makeText(AnadirServicioActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
