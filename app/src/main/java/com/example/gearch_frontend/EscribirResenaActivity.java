package com.example.gearch_frontend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Resena;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla para escribir una resena sobre un taller
// Recibe el tallerId desde DetalleTallerActivity
public class EscribirResenaActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etComentario;
    private Button btnEnviar;
    private ApiService api;
    private Long usuarioId;
    private Long tallerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribir_resena);

        ratingBar = findViewById(R.id.ratingBar);
        etComentario = findViewById(R.id.etComentario);
        btnEnviar = findViewById(R.id.btnEnviar);

        api = ApiClient.getClient().create(ApiService.class);

        // Recibimos el id del taller que nos paso DetalleTallerActivity
        tallerId = getIntent().getLongExtra("tallerId", -1);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        usuarioId = prefs.getLong("id", -1);

        btnEnviar.setOnClickListener(v -> enviarResena());
    }

    private void enviarResena() {
        String comentario = etComentario.getText().toString().trim();
        int puntuacion = (int) ratingBar.getRating();

        if (puntuacion == 0) {
            Toast.makeText(this, "Selecciona una puntuacion", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comentario.isEmpty()) {
            Toast.makeText(this, "Escribe un comentario", Toast.LENGTH_SHORT).show();
            return;
        }

        Resena resena = new Resena();
        resena.setPuntuacion(puntuacion);
        resena.setComentario(comentario);

        api.crearResena(usuarioId, tallerId, resena).enqueue(new Callback<Resena>() {
            @Override
            public void onResponse(Call<Resena> call, Response<Resena> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EscribirResenaActivity.this, "Resena enviada correctamente", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EscribirResenaActivity.this, "Error al enviar la resena", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Resena> call, Throwable t) {
                Toast.makeText(EscribirResenaActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
