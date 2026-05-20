package com.example.gearch_frontend;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Taller;
import com.example.gearch_frontend.api.models.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla de registro para nuevos usuarios
// Permite registrarse como cliente o como propietario de taller
// Si se elige propietario se muestran campos adicionales para el taller
public class RegisterActivity extends AppCompatActivity {

    private RadioGroup rgTipoUsuario;
    private LinearLayout layoutTaller;
    private ApiService api;
    private EditText etNombre, etApellidos, etEmail, etPassword, etTelefono;
    private EditText etNombreTaller, etDireccion, etTelefonoTaller, etDescripcion;
    private Button btnRegistrar;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rgTipoUsuario = findViewById(R.id.rgTipoUsuario);
        layoutTaller = findViewById(R.id.layoutTaller);
        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etTelefono = findViewById(R.id.etTelefono);
        etNombreTaller = findViewById(R.id.etNombreTaller);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefonoTaller = findViewById(R.id.etTelefonoTaller);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvLogin = findViewById(R.id.tvLogin);

        // Mostramos u ocultamos los campos del taller segun el radio button seleccionado
        rgTipoUsuario.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPropietario) {
                layoutTaller.setVisibility(View.VISIBLE);
            } else {
                layoutTaller.setVisibility(View.GONE);
            }
        });

        btnRegistrar.setOnClickListener(v -> registrar());

        // Al pulsar el texto de login volvemos atras
        tvLogin.setOnClickListener(v -> finish());
    }

    private void registrar() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validamos que el email tenga un formato correcto
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Introduce un email válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setTelefono(telefono);

        api = ApiClient.getClient().create(ApiService.class);

        if (rgTipoUsuario.getCheckedRadioButtonId() == R.id.rbCliente) {
            registrarCliente(api, usuario);
        } else {
            registrarPropietario(api, usuario);
        }
    }

    private void registrarCliente(ApiService api, Usuario usuario) {
        api.registrarCliente(usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registro correcto, inicia sesion", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error al registrar, ese email ya esta en uso", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarPropietario(ApiService api, Usuario usuario) {
        String nombreTaller = etNombreTaller.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefonoTaller = etTelefonoTaller.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (nombreTaller.isEmpty() || direccion.isEmpty() || telefonoTaller.isEmpty()) {
            Toast.makeText(this, "Rellena los datos del taller", Toast.LENGTH_SHORT).show();
            return;
        }

        Taller taller = new Taller();
        taller.setNombre(nombreTaller);
        taller.setDireccion(direccion);
        taller.setTelefono(telefonoTaller);
        taller.setDescripcion(descripcion);

        // Geocoder convierte la direccion escrita por el usuario en coordenadas (latitud y longitud)
        // Si falla se guarda el taller sin coordenadas, no aparece en cercanos pero si en buscar
        try {
            Geocoder geocoder = new Geocoder(RegisterActivity.this);
            // El 1 indica que solo queremos 1 coincidencia de la direccion
            List<Address> direcciones = geocoder.getFromLocationName(direccion, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                taller.setLatitud(direcciones.get(0).getLatitude());
                taller.setLongitud(direcciones.get(0).getLongitude());
            }
        } catch (IOException e) {
            Toast.makeText(RegisterActivity.this, "No se pudieron obtener las coordenadas", Toast.LENGTH_SHORT).show();
        }

        // El backend espera un objeto con "usuario" y "taller" anidados
        Map<String, Object> request = new HashMap<>();
        request.put("usuario", usuario);
        request.put("taller", taller);

        api.registrarAdminTaller(request).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registro correcto, inicia sesión", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error al registrar, ese email ya está en uso", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}