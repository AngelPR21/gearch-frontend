package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MultipartBody;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Taller;

import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarTallerActivity extends AppCompatActivity {

    private ImageView ivFotoTaller;
    private Button btnCambiarFoto, btnGuardar;
    private EditText etNombre, etDireccion, etTelefono, etDescripcion;
    private ApiService api;
    private Long adminId;
    private Taller tallerActual;

    // Launcher para seleccionar foto de la galería
    ActivityResultLauncher<String> seleccionarFotoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    ivFotoTaller.setImageURI(uri);
                    subirFoto(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_taller);

        ivFotoTaller = findViewById(R.id.ivFotoTaller);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);
        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);
        etDescripcion = findViewById(R.id.etDescripcion);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        adminId = prefs.getLong("id", -1);

        cargarTaller();

        btnCambiarFoto.setOnClickListener(v -> seleccionarFotoLauncher.launch("image/*"));

        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

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

                    // Si tiene foto la mostramos, si no dejamos la por defecto
                    if (tallerActual.getFotoPerfil() != null) {
                        ivFotoTaller.setImageBitmap(BitmapFactory.decodeByteArray(
                                tallerActual.getFotoPerfil(), 0, tallerActual.getFotoPerfil().length));
                    }
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

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Nombre, dirección y teléfono son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mantenemos la latitud y longitud actuales ya que no se editan en esta pantalla
        Taller taller = new Taller();
        taller.setNombre(nombre);
        taller.setDireccion(direccion);
        taller.setTelefono(telefono);
        taller.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        taller.setLatitud(tallerActual.getLatitud());
        taller.setLongitud(tallerActual.getLongitud());

        api.actualizarTaller(adminId, taller).enqueue(new Callback<Taller>() {
            @Override
            public void onResponse(Call<Taller> call, Response<Taller> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditarTallerActivity.this, "Taller actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditarTallerActivity.this, "Error al actualizar el taller", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Taller> call, Throwable t) {
                Toast.makeText(EditarTallerActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subirFoto(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), bytes);
            MultipartBody.Part part = MultipartBody.Part.createFormData("foto", "foto.jpg", requestBody);

            api.subirFotoTaller(adminId, part).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditarTallerActivity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditarTallerActivity.this, "Error al subir la foto", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditarTallerActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error al leer la foto", Toast.LENGTH_SHORT).show();
        }
    }
}