package com.example.gearch_frontend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Vehiculo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla para anadir un nuevo vehiculo al perfil del usuario
// Al guardar correctamente devuelve RESULT_OK a MisVehiculosActivity para que recargue la lista
public class AnadirVehiculoActivity extends AppCompatActivity {

    private EditText etMarca, etModelo, etMatricula, etAnio, etColor, etCombustible;
    private Button btnGuardar;
    private ApiService api;
    private Long usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_vehiculo);

        etMarca = findViewById(R.id.etMarca);
        etModelo = findViewById(R.id.etModelo);
        etMatricula = findViewById(R.id.etMatricula);
        etAnio = findViewById(R.id.etAnio);
        etColor = findViewById(R.id.etColor);
        etCombustible = findViewById(R.id.etCombustible);
        btnGuardar = findViewById(R.id.btnGuardar);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        usuarioId = prefs.getLong("id", -1);

        btnGuardar.setOnClickListener(v -> guardarVehiculo());
    }

    private void guardarVehiculo() {
        String marca = etMarca.getText().toString().trim();
        String modelo = etModelo.getText().toString().trim();
        String matricula = etMatricula.getText().toString().trim();
        String anioStr = etAnio.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String combustible = etCombustible.getText().toString().trim();

        // Marca, modelo y matricula son obligatorios
        if (marca.isEmpty() || modelo.isEmpty() || matricula.isEmpty()) {
            Toast.makeText(this, "Marca, modelo y matricula son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setMatricula(matricula);
        vehiculo.setColor(color.isEmpty() ? null : color);
        vehiculo.setCombustible(combustible.isEmpty() ? null : combustible);

        // El anio es opcional
        if (!anioStr.isEmpty()) {
            vehiculo.setAnio(Integer.parseInt(anioStr));
        }

        api.crearVehiculo(usuarioId, vehiculo).enqueue(new Callback<Vehiculo>() {
            @Override
            public void onResponse(Call<Vehiculo> call, Response<Vehiculo> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AnadirVehiculoActivity.this, "Vehiculo anadido correctamente", Toast.LENGTH_SHORT).show();
                    // RESULT_OK indica a MisVehiculosActivity que recargue la lista
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AnadirVehiculoActivity.this, "Error al anadir el vehiculo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Vehiculo> call, Throwable t) {
                Toast.makeText(AnadirVehiculoActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
