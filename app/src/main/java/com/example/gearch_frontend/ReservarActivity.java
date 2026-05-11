package com.example.gearch_frontend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.adapters.HoraAdapter;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Cita;
import com.example.gearch_frontend.api.models.Servicio;
import com.example.gearch_frontend.api.models.Vehiculo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla para reservar una cita en un taller
// Permite seleccionar fecha en el CalendarView, hora en el grid de horas,
// servicio y vehiculo (opcional) en los Spinners
public class ReservarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView rvHoras;
    private Spinner spinnerServicios, spinnerVehiculos;
    private EditText etNotas;
    private Button btnConfirmar;

    private ApiService api;
    private HoraAdapter horaAdapter;

    private Long tallerId;
    private Long usuarioId;

    // Fecha seleccionada en el calendario en formato yyyy-MM-dd para el backend
    private String fechaSeleccionada;

    private List<Servicio> listaServicios = new ArrayList<>();
    private List<Vehiculo> listaVehiculos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar);

        calendarView = findViewById(R.id.calendarView);
        rvHoras = findViewById(R.id.rvHoras);
        spinnerServicios = findViewById(R.id.spinnerServicios);
        spinnerVehiculos = findViewById(R.id.spinnerVehiculos);
        etNotas = findViewById(R.id.etNotas);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        api = ApiClient.getClient().create(ApiService.class);

        // Recibimos el id del taller que nos paso DetalleTallerActivity
        tallerId = getIntent().getLongExtra("tallerId", -1);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        usuarioId = prefs.getLong("id", -1);

        // Inicializamos la fecha con el dia de hoy usando Calendar (compatible con API 24+)
        Calendar calendar = Calendar.getInstance();
        fechaSeleccionada = String.format("%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, // MONTH va en base 0, enero = 0
                calendar.get(Calendar.DAY_OF_MONTH));

        // Inicializamos el adapter de horas vacio
        // Se rellena cuando llega la respuesta de getHorasLibres
        horaAdapter = new HoraAdapter(this, new ArrayList<>());
        rvHoras.setAdapter(horaAdapter);

        cargarServicios();
        cargarVehiculos();
        cargarHorasDisponibles(fechaSeleccionada);

        // Cuando el usuario cambia de dia en el calendario recargamos las horas
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // month viene en base 0, sumamos 1 para el formato correcto
            fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            cargarHorasDisponibles(fechaSeleccionada);
        });

        btnConfirmar.setOnClickListener(v -> confirmarReserva());
    }

    // Carga los servicios del taller y rellena el Spinner
    private void cargarServicios() {
        api.getServiciosByTaller(tallerId).enqueue(new Callback<List<Servicio>>() {
            @Override
            public void onResponse(Call<List<Servicio>> call, Response<List<Servicio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaServicios = response.body();

                    // Extraemos los nombres y precios para mostrarlos en el Spinner
                    List<String> nombres = new ArrayList<>();
                    for (Servicio s : listaServicios) {
                        nombres.add(s.getNombre() + " - " + s.getPrecio() + " €");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ReservarActivity.this,
                            android.R.layout.simple_spinner_item,
                            nombres
                    );
                    // setDropDownViewResource define el layout de la lista desplegable del Spinner
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerServicios.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Servicio>> call, Throwable t) {
                Toast.makeText(ReservarActivity.this, "Error al cargar servicios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Carga los vehiculos del usuario y rellena el Spinner
    private void cargarVehiculos() {
        api.getVehiculosUsuario(usuarioId).enqueue(new Callback<List<Vehiculo>>() {
            @Override
            public void onResponse(Call<List<Vehiculo>> call, Response<List<Vehiculo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaVehiculos = response.body();

                    // La primera opcion es "Sin especificar" porque el vehiculo es opcional
                    List<String> opciones = new ArrayList<>();
                    opciones.add("Sin especificar");
                    for (Vehiculo v : listaVehiculos) {
                        opciones.add(v.getMarca() + " " + v.getModelo() + " - " + v.getMatricula());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ReservarActivity.this,
                            android.R.layout.simple_spinner_item,
                            opciones
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerVehiculos.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Vehiculo>> call, Throwable t) {
                Toast.makeText(ReservarActivity.this, "Error al cargar vehiculos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Llama al backend con la fecha seleccionada y actualiza el RecyclerView de horas
    private void cargarHorasDisponibles(String fecha) {
        api.getHorasLibres(tallerId, fecha).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    horaAdapter.actualizarHoras(response.body());
                } else {
                    horaAdapter.actualizarHoras(new ArrayList<>());
                    Toast.makeText(ReservarActivity.this, "No hay horas disponibles para este dia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                horaAdapter.actualizarHoras(new ArrayList<>());
                Toast.makeText(ReservarActivity.this, "Error al cargar horas disponibles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Valida los datos y envia la cita al backend
    private void confirmarReserva() {
        String horaSeleccionada = horaAdapter.getHoraSeleccionada();
        if (horaSeleccionada == null) {
            Toast.makeText(this, "Selecciona una hora", Toast.LENGTH_SHORT).show();
            return;
        }

        int posServicio = spinnerServicios.getSelectedItemPosition();
        Servicio servicioSeleccionado = listaServicios.get(posServicio);

        // Posicion 0 = "Sin especificar", vehiculoId sera null (el backend lo acepta)
        int posVehiculo = spinnerVehiculos.getSelectedItemPosition();
        Long vehiculoId = null;
        if (posVehiculo != 0) {
            vehiculoId = listaVehiculos.get(posVehiculo - 1).getId();
        }

        // Combinamos fecha y hora en el formato que espera el backend: "yyyy-MM-ddTHH:mm:ss"
        // El backend lo deserializa como LocalDateTime
        Cita cita = new Cita();
        cita.setFechaHora(fechaSeleccionada + "T" + horaSeleccionada);
        cita.setNotas(etNotas.getText().toString().trim());

        api.crearCita(usuarioId, tallerId, servicioSeleccionado.getId(), vehiculoId, cita)
                .enqueue(new Callback<Cita>() {
                    @Override
                    public void onResponse(Call<Cita> call, Response<Cita> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ReservarActivity.this, "Reserva realizada correctamente", Toast.LENGTH_LONG).show();
                            finish(); // Volvemos a DetalleTallerActivity
                        } else {
                            Toast.makeText(ReservarActivity.this, "Error al crear la cita", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Cita> call, Throwable t) {
                        Toast.makeText(ReservarActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
