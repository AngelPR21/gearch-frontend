package com.example.gearch_frontend;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
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

// Pantalla para anadir un nuevo dia al horario del taller
// El admin selecciona el dia de la semana y las horas de apertura y cierre
// Al guardar correctamente devuelve RESULT_OK a HorarioAdminActivity para que recargue
public class AnadirHorarioActivity extends AppCompatActivity {

    private Spinner spinnerDia;
    private Button btnHoraInicio, btnHoraFin, btnGuardar;
    private ApiService api;
    private long adminId;

    // Arrays para guardar las horas seleccionadas en los TimePicker
    // Inicializamos con valores por defecto: 09:00 y 18:00
    private int[] horaInicio = {9, 0};
    private int[] horaFin = {18, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_horario);

        // Boton de volver atras
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        spinnerDia = findViewById(R.id.spinnerDia);
        btnHoraInicio = findViewById(R.id.btnHoraInicio);
        btnHoraFin = findViewById(R.id.btnHoraFin);
        btnGuardar = findViewById(R.id.btnGuardar);

        api = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        adminId = prefs.getLong("id", -1);

        // Rellenamos el Spinner con los dias de la semana del enum DiaSemana
        ArrayAdapter<DiaSemana> adapterDias = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, DiaSemana.values());
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDia.setAdapter(adapterDias);

        // Al pulsar el boton de hora inicio abre un TimePickerDialog
        btnHoraInicio.setOnClickListener(v -> new TimePickerDialog(this, (view, h, m) -> {
            horaInicio[0] = h;
            horaInicio[1] = m;
            btnHoraInicio.setText(String.format("%02d:%02d", h, m));
        }, horaInicio[0], horaInicio[1], true).show());

        // Al pulsar el boton de hora fin abre un TimePickerDialog
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
        // El backend espera el formato "HH:mm:ss"
        disponibilidad.setHoraInicio(String.format("%02d:%02d:00", horaInicio[0], horaInicio[1]));
        disponibilidad.setHoraFin(String.format("%02d:%02d:00", horaFin[0], horaFin[1]));
        // El intervalo es fijo a 30 minutos
        disponibilidad.setIntervaloMinutos(30);

        api.crearHorario(adminId, disponibilidad).enqueue(new Callback<DisponibilidadTaller>() {
            @Override
            public void onResponse(Call<DisponibilidadTaller> call, Response<DisponibilidadTaller> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AnadirHorarioActivity.this, "Dia anadido correctamente", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AnadirHorarioActivity.this, "Error al anadir el dia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DisponibilidadTaller> call, Throwable t) {
                Toast.makeText(AnadirHorarioActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
