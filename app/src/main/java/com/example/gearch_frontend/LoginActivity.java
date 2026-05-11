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
import com.example.gearch_frontend.api.models.Usuario;
import com.example.gearch_frontend.api.models.enums.RolUsuario;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla de inicio de sesion
// Redirige a MainClienteActivity o MainAdminActivity segun el rol del usuario
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ApiService api;
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
        tvRegistro.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("email", email);
        credenciales.put("password", password);

        api = ApiClient.getClient().create(ApiService.class);
        api.login(credenciales).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();

                    // Guardamos los datos de sesion en SharedPreferences
                    // SharedPreferences es como el LocalStorage de JavaScript, guarda datos simples en un fichero XML interno
                    SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("id", usuario.getId());
                    editor.putString("rol", usuario.getRol().name());

                    // Si es admin guardamos el id del taller con la clave "tallerId"
                    // Esta clave se usa en MainAdminActivity, CitasAdminActivity y demas pantallas admin
                    if (usuario.getTallerAdministradoId() != null) {
                        editor.putLong("tallerId", usuario.getTallerAdministradoId());
                    }
                    editor.apply();

                    // Redirigimos segun el rol del usuario
                    if (usuario.getRol() == RolUsuario.ADMIN_TALLER) {
                        startActivity(new Intent(LoginActivity.this, MainAdminActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainClienteActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Email o contrasena incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
