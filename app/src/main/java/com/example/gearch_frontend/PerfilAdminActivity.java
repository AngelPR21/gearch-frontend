package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla de perfil del admin
// Permite editar datos personales y eliminar la cuenta
// Al eliminar la cuenta tambien se elimina el taller y todos sus datos (cascada en el backend)
public class PerfilAdminActivity extends AppCompatActivity {

    private EditText etNombre, etApellidos, etEmail, etTelefono;
    private Button btnGuardar, btnEliminarCuenta;
    private ApiService api;
    private Long adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_admin);

        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        adminId = prefs.getLong("id", -1);

        cargarPerfil();

        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnEliminarCuenta.setOnClickListener(v -> confirmarEliminarCuenta());
    }

    // Carga los datos del admin y los muestra en los EditText
    private void cargarPerfil() {
        api.getUsuario(adminId).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    etNombre.setText(usuario.getNombre());
                    etApellidos.setText(usuario.getApellidos());
                    etEmail.setText(usuario.getEmail());
                    etTelefono.setText(usuario.getTelefono());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PerfilAdminActivity.this, "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarCambios() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nombre, apellidos y email son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setTelefono(telefono.isEmpty() ? null : telefono);

        api.actualizarUsuario(adminId, usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PerfilAdminActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PerfilAdminActivity.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PerfilAdminActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Muestra un dialogo de confirmacion antes de eliminar la cuenta
    // Al eliminar la cuenta tambien se borra el taller y todos sus datos
    private void confirmarEliminarCuenta() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar cuenta")
                .setMessage("Esta accion eliminara tu cuenta y tu taller con todos sus datos. No se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarCuenta())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarCuenta() {
        api.eliminarUsuario(adminId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
                    prefs.edit().clear().apply();
                    Intent intent = new Intent(PerfilAdminActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(PerfilAdminActivity.this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PerfilAdminActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}