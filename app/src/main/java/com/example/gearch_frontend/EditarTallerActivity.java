package com.example.gearch_frontend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Taller;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla del admin para editar los datos de su taller
// La latitud y longitud se mantienen igual porque no se pueden editar desde esta pantalla
public class EditarTallerActivity extends AppCompatActivity {

    private Button btnGuardar;
    private EditText etNombre, etDireccion, etTelefono, etDescripcion;
    private ApiService api;
    private long adminId;
    private Taller tallerActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_taller);

        // Boton de volver atras
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        btnGuardar = findViewById(R.id.btnGuardar);
        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        etDescripcion = findViewById(R.id.etDescripcion);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        adminId = prefs.getLong("id", -1);

        cargarTaller();

        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

    // Carga los datos actuales del taller y los muestra en los EditText
    private void cargarTaller() {
        api.getMiTaller(adminId).enqueue(new Callback<Taller>() {
            @Override
            public void onResponse(Call<Taller> call, Response<Taller> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tallerActual = response.body();
                    etNombre.setText(tallerActual.getNombre());
                    etDireccion.setText(tallerActual.getDireccion());
                    etTelefono.setText(tallerActual.getTelefono());
                    etDescripcion.setText(tallerActual.getDescripcion());
                }
            }

            @Override
            public void onFailure(Call<Taller> call, Throwable t) {
                Toast.makeText(EditarTallerActivity.this, "Error al cargar el taller", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarCambios() {
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (tallerActual == null) {
            Toast.makeText(this, "Error: taller no cargado", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Nombre, direccion y telefono son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Taller taller = new Taller();
        taller.setNombre(nombre);
        taller.setDireccion(direccion);
        taller.setTelefono(telefono);
        taller.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        // Mantenemos las coordenadas actuales ya que no se pueden editar desde esta pantalla
        taller.setLatitud(tallerActual.getLatitud());
        taller.setLongitud(tallerActual.getLongitud());

        api.actualizarTaller(adminId, taller).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditarTallerActivity.this, "Taller actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditarTallerActivity.this, "Error al actualizar el taller", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditarTallerActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}