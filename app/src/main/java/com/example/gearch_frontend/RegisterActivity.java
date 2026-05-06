package com.example.gearch_frontend;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Geocoder;
import android.location.Address;

import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Taller;
import com.example.gearch_frontend.api.models.Usuario;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private RadioGroup rgTipoUsuario;
    private LinearLayout layoutTaller;
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

        // Mostrar u ocultar los campos del taller según el radio button seleccionado
        rgTipoUsuario.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPropietario) {
                layoutTaller.setVisibility(View.VISIBLE);
            } else {
                layoutTaller.setVisibility(View.GONE);
            }
        });

        btnRegistrar.setOnClickListener(v -> registrar());
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

        // Construir el usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setTelefono(telefono);

        ApiService api = ApiClient.getClient().create(ApiService.class);

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

    private void registrarPropietario(ApiService api, Usuario usuario) {
        String nombreTaller = etNombreTaller.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefonoTaller = etTelefonoTaller.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (nombreTaller.isEmpty() || direccion.isEmpty() || telefonoTaller.isEmpty()) {
            Toast.makeText(this, "Rellena los datos del taller", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construir el taller
        Taller taller = new Taller();
        taller.setNombre(nombreTaller);
        taller.setDireccion(direccion);
        taller.setTelefono(telefonoTaller);
        taller.setDescripcion(descripcion);

        // Convertir dirección a coordenadas
        try{
            Geocoder geocoder = new Geocoder(RegisterActivity.this); //Esto es simplemente para que pase el nombre de la direccion a coordenadas( geocodificacion)
            List<Address> direcciones = geocoder.getFromLocationName(direccion, 1);//el 1 es para que devuelva 1 coincidencia de esa direccion ya que pueden haber varias
            if (direcciones != null && !direcciones.isEmpty()) {
                taller.setLatitud(direcciones.get(0).getLatitude());
                taller.setLongitud(direcciones.get(0).getLongitude());
            }
        }catch(IOException e){
            //Si falla se puede añadir a la bbdd como null, no aparecera en cercanos pero se puede buscar igual
            Toast.makeText(RegisterActivity.this, "No se pudieron obtener las coordenadas", Toast.LENGTH_SHORT).show();

        }


        // El backend espera el record con el usuario y taller
        Map<String, Object> request = new HashMap<>();
        request.put("usuario", usuario);
        request.put("taller", taller);

        api.registrarAdminTaller(request).enqueue(new Callback<Usuario>() {//callback espera recibir un objeto usuario cuando el servidor responda
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