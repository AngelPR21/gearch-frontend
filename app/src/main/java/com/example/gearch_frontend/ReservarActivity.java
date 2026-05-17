package com.example.gearch_frontend;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    private GridLayout gridHoras;
    private Spinner spinnerServicios, spinnerVehiculos;
    private EditText etNotas;
    private Button btnConfirmar;

    private ApiService api;

    private long tallerId;
    private long usuarioId;

    // Fecha seleccionada en el calendario en formato yyyy-MM-dd para el backend
    private String fechaSeleccionada;
    // Hora seleccionada por el usuario, null si no ha seleccionado ninguna
    private String horaSeleccionada = null;
    // Boton actualmente seleccionado para poder deseleccionarlo al pulsar otro
    private Button botonSeleccionado = null;

    private List<Servicio> listaServicios = new ArrayList<>();
    private List<Vehiculo> listaVehiculos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar);

        // Boton de volver atras
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        calendarView = findViewById(R.id.calendarView);
        gridHoras = findViewById(R.id.gridHoras);
        spinnerServicios = findViewById(R.id.spinnerServicios);
        spinnerVehiculos = findViewById(R.id.spinnerVehiculos);
        etNotas = findViewById(R.id.etNotas);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        api = ApiClient.getClient().create(ApiService.class);

        tallerId = getIntent().getLongExtra("tallerId", -1);

        SharedPreferences prefs = getSharedPreferences("gearch", MODE_PRIVATE);
        usuarioId = prefs.getLong("id", -1);

        // Inicializamos la fecha con el dia de hoy usando Calendar (compatible con API 24+)
        Calendar calendar = Calendar.getInstance();
        fechaSeleccionada = String.format("%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));

        // No permitir seleccionar fechas pasadas y forzar que muestre hoy
        calendarView.setMinDate(System.currentTimeMillis());
        calendarView.setDate(System.currentTimeMillis(), false, true);

        cargarServicios();
        cargarVehiculos();
        cargarHorasDisponibles(fechaSeleccionada);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            cargarHorasDisponibles(fechaSeleccionada);
        });

        btnConfirmar.setOnClickListener(v -> confirmarReserva());
    }

    // Rellena el GridLayout con un boton por cada hora disponible
    // Al pulsar un boton lo marca en naranja y guarda la hora seleccionada
    private void mostrarHorasEnGrid(List<String> horas) {
        gridHoras.removeAllViews();
        horaSeleccionada = null;
        botonSeleccionado = null;

        for (String hora : horas) {
            // Mostramos solo HH:mm aunque el backend devuelva HH:mm:ss
            String horaCorta = hora.length() >= 5 ? hora.substring(0, 5) : hora;

            Button btn = new Button(this);
            btn.setText(horaCorta);
            btn.setTextSize(13);
            btn.setTypeface(null, Typeface.BOLD);
            btn.setTextColor(Color.BLACK);
            btn.setBackgroundColor(Color.WHITE);

            // Parametros para que cada boton ocupe 1/3 del ancho del grid
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            params.setMargins(4, 4, 4, 4);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                // Deseleccionamos el boton anterior si habia uno
                if (botonSeleccionado != null) {
                    botonSeleccionado.setBackgroundColor(Color.WHITE);
                    botonSeleccionado.setTextColor(Color.BLACK);
                }
                // Marcamos el boton pulsado en naranja
                btn.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                btn.setTextColor(Color.WHITE);
                botonSeleccionado = btn;
                horaSeleccionada = hora; // guardamos la hora completa HH:mm:ss
            });

            gridHoras.addView(btn);
        }
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

    // Llama al backend con la fecha seleccionada y actualiza el grid de horas
    private void cargarHorasDisponibles(String fecha) {
        api.getHorasLibres(tallerId, fecha).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        mostrarHorasEnGrid(new ArrayList<>());
                        Toast.makeText(ReservarActivity.this, "El taller no tiene horario este dia", Toast.LENGTH_SHORT).show();
                    } else {
                        mostrarHorasEnGrid(response.body());
                    }
                } else {
                    mostrarHorasEnGrid(new ArrayList<>());
                    Toast.makeText(ReservarActivity.this, "Error al cargar horas disponibles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                mostrarHorasEnGrid(new ArrayList<>());
                Toast.makeText(ReservarActivity.this, "Error al cargar horas disponibles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Valida los datos y envia la cita al backend
    private void confirmarReserva() {
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

        // fechaSeleccionada viene como "yyyy-MM-dd", la convertimos a "dd/MM/yyyy"
        // El backend espera el formato "dd/MM/yyyy HH:mm" segun el @JsonFormat de Cita.java
        String[] partesFecha = fechaSeleccionada.split("-");
        String fechaFormateada = partesFecha[2] + "/" + partesFecha[1] + "/" + partesFecha[0];

        // Recortamos los segundos si la hora viene como "HH:mm:ss" desde el backend
        String horaSinSegundos = horaSeleccionada.length() > 5 ? horaSeleccionada.substring(0, 5) : horaSeleccionada;

        Cita cita = new Cita();
        cita.setFechaHora(fechaFormateada + " " + horaSinSegundos);
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