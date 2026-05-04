package com.example.gearch_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegistro = findViewById(R.id.tvRegistro);

        btnLogin.setOnClickListener(v -> login());

        // Al pulsar el texto de registro abre RegisterActivity
        tvRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validación básica
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construir el body de la petición
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("email", email);
        credenciales.put("password", password);

        // Llamada al backend
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.login(credenciales).enqueue(new Callback<Map<String, Object>>() {

            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {

                // Si el servidor ha respondido correctamente
                if (response.isSuccessful() && response.body() != null) {

                    Map<String, Object> usuario = response.body();
                    String rol = (String) usuario.get("rol");
                    int id = ((Double) usuario.get("id")).intValue();

                    // Guardamos el id y el rol para usarlos en otras pantallas
                    SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
                    prefs.edit().putInt("id", id).putString("rol", rol).apply();

                    // Si es admin del taller guardamos también el id del taller
                    if (usuario.containsKey("tallerAdministradoId")) {
                        int tallerAdministradoId = ((Double) usuario.get("tallerAdministradoId")).intValue();
                        prefs.edit().putInt("tallerAdministradoId", tallerAdministradoId).apply();
                    }

                    // Redirigir según el rol
                    if ("ADMIN_TALLER".equals(rol)) {
                        startActivity(new Intent(LoginActivity.this, MainAdminActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainClienteActivity.class));
                    }
                    finish(); // Cierra el login para no volver atrás

                } else {
                    // El servidor ha respondido pero con error (credenciales incorrectas)
                    Toast.makeText(LoginActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // Error de conexión, el backend no está arrancado
                Toast.makeText(LoginActivity.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}