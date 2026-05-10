package com.example.gearch_frontend;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.DisponibilidadTaller;
import com.example.gearch_frontend.api.models.enums.DiaSemana;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnadirHorarioActivity extends AppCompatActivity {

    private Spinner spinnerDia;
    private Button btnHoraInicio, btnHoraFin, btnGuardar;
    private ApiService api;
    private Long adminId;

    // Guardamos las horas seleccionadas
    private int[] horaInicio = {9, 0};
    private int[] horaFin = {18, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_horario);

        spinnerDia = findViewById(R.id.spinnerDia);
        btnHoraInicio = findViewById(R.id.btnHoraInicio);
        btnHoraFin = findViewById(R.id.btnHoraFin);
        btnGuardar = findViewById(R.id.btnGuardar);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        adminId = prefs.getLong("id", -1);

        // Rellenamos el Spinner con los días de la semana
        ArrayAdapter<DiaSemana> adapterDias = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, DiaSemana.values());
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDia.setAdapter(adapterDias);

        // Al pulsar el botón de hora inicio abre un TimePicker
        btnHoraInicio.setOnClickListener(v -> new TimePickerDialog(this, (view, h, m) -> {
            horaInicio[0] = h;
            horaInicio[1] = m;
            btnHoraInicio.setText(String.format("%02d:%02d", h, m));
        }, horaInicio[0], horaInicio[1], true).show());

        // Al pulsar el botón de hora fin abre un TimePicker
        btnHoraFin.setOnClickListener(v -> new TimePickerDialog(this, (view, h, m) -> {
            horaFin[0] = h;
            horaFin[1] = m;
            btnHoraFin.setText(String.format("%02d:%02d", h, m));
        }, horaFin[0], horaFin[1], true).show());

        btnGuardar.setOnClickListener(v -> guardarHorario());
    }

    private void guardarHorario() {
        DiaSemana dia = (DiaSemana) spinnerDia.getSelectedItem();

        DisponibilidadTaller disponibilidad = new DisponibilidadTaller();
        disponibilidad.setDiaSemana(dia);
        disponibilidad.setHoraInicio(String.format("%02d:%02d:00", horaInicio[0], horaInicio[1]));
        disponibilidad.setHoraFin(String.format("%02d:%02d:00", horaFin[0], horaFin[1]));
        disponibilidad.setIntervaloMinutos(30);

        api.crearHorario(adminId, disponibilidad).enqueue(new Callback<DisponibilidadTaller>() {
            @Override
            public void onResponse(Call<DisponibilidadTaller> call, Response<DisponibilidadTaller> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AnadirHorarioActivity.this, "Día añadido correctamente", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AnadirHorarioActivity.this, "Error al añadir el día", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DisponibilidadTaller> call, Throwable t) {
                Toast.makeText(AnadirHorarioActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}