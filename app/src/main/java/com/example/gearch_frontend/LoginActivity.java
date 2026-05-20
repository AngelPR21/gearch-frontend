package com.example.gearch_frontend;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Usuario;
import com.example.gearch_frontend.api.models.enums.RolUsuario;
import com.google.firebase.messaging.FirebaseMessaging;

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

        // Si ya hay sesion guardada saltamos directamente a la pantalla principal
        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        String rol = prefs.getString("rol", null);
        if (rol != null) {
            if (rol.equals("ADMIN_TALLER")) {
                startActivity(new Intent(this, MainAdminActivity.class));
            } else {
                startActivity(new Intent(this, MainClienteActivity.class));
            }
            finish(); // cierra LoginActivity para que no quede en el historial de navegacion
            return;   // evita ejecutar el resto del metodo onCreate
        }

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

                    // Obtenemos el token de Firebase del dispositivo y lo enviamos al backend
                    // El token puede cambiar si el usuario reinstala la app o Firebase lo renueva
                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                        //operacion asincrona, cuando responda si el token es distinto de null llama al patch para añadir el token al usuario
                        if (token != null) {
                            //lo actualiza siempre que se inicia sesion por si FCM lo ha cambiado
                            api.actualizarFcmToken(usuario.getId(), token).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    // No hacemos nada si falla, no es critico para el login
                                }
                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    // No hacemos nada si falla, no es critico para el login
                                }
                            });
                        }
                    });

                    // Si es admin guardamos el id del taller con la clave "tallerId"
                    // Esta clave se usa en MainAdminActivity, CitasAdminActivity y demas pantallas admin
                    if (usuario.getTallerAdministradoId() != null) {
                        editor.putLong("tallerId", usuario.getTallerAdministradoId().longValue());
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
                    Toast.makeText(LoginActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}