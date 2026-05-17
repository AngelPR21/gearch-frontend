package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla de perfil del usuario
// Permite ver y editar datos personales, cerrar sesion y eliminar cuenta
public class PerfilActivity extends AppCompatActivity {

    private EditText etNombre, etApellidos, etEmail, etTelefono;
    private Button btnGuardar, btnCerrarSesion, btnEliminarCuenta;
    private ApiService api;
    private long usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        usuarioId = prefs.getLong("id", -1);

        // Botones de navegacion inferior
        ImageButton ibHome = findViewById(R.id.btnHome);
        ImageButton ibCitas = findViewById(R.id.btnCitas);
        ImageButton ibBuscar = findViewById(R.id.btnBuscar);
        ImageButton ibVehiculos = findViewById(R.id.btnVehiculos);

        ibHome.setOnClickListener(v -> startActivity(new Intent(this, MainClienteActivity.class)));
        ibCitas.setOnClickListener(v -> startActivity(new Intent(this, MisCitasActivity.class)));
        ibBuscar.setOnClickListener(v -> startActivity(new Intent(this, BuscarActivity.class)));
        ibVehiculos.setOnClickListener(v -> startActivity(new Intent(this, MisVehiculosActivity.class)));

        cargarPerfil();

        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        btnEliminarCuenta.setOnClickListener(v -> confirmarEliminarCuenta());
    }

    // Carga los datos del usuario y los muestra en los EditText
    private void cargarPerfil() {
        api.getUsuario(usuarioId).enqueue(new Callback<Usuario>() {
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
                Toast.makeText(PerfilActivity.this, "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Guarda los cambios del perfil en el backend
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

        api.actualizarUsuario(usuarioId, usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PerfilActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PerfilActivity.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Borra el token FCM del backend y limpia las SharedPreferences
    private void cerrarSesion() {
        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);

        // Borramos el token FCM del backend para que no lleguen notificaciones a este dispositivo
        if (usuarioId != -1) {
            api.actualizarFcmToken(usuarioId, "").enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {}
                @Override
                public void onFailure(Call<Void> call, Throwable t) {}
            });
        }

        prefs.edit().clear().apply();
        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        /*
        Limpiamos el back stack para que no pueda volver atras con el boton de retroceso

        Los dos juntos consiguen que al cerrar sesion el usuario no pueda pulsar el boton de atras
        del movil y volver a una pantalla de la app sin estar logueado.
        Sin estos flags podria volver atras y seguir viendo pantallas aunque ya haya cerrado sesion.

        Usa el | (OR) porque es la forma de combinar ambos
         */
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Muestra un dialogo de confirmacion antes de eliminar la cuenta
    private void confirmarEliminarCuenta() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar cuenta")
                .setMessage("Esta accion no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarCuenta())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarCuenta() {
        api.eliminarUsuario(usuarioId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
                    prefs.edit().clear().apply();
                    Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(PerfilActivity.this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PerfilActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}