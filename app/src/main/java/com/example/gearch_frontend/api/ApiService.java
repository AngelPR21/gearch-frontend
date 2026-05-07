package com.example.gearch_frontend.api;

import com.example.gearch_frontend.api.models.Cita;
import com.example.gearch_frontend.api.models.Resena;
import com.example.gearch_frontend.api.models.Servicio;
import com.example.gearch_frontend.api.models.Taller;
import com.example.gearch_frontend.api.models.Usuario;
import com.example.gearch_frontend.api.models.Vehiculo;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // POST /api/usuarios/login
    @POST("api/usuarios/login")
    Call<Usuario> login(@Body Map<String, String> credenciales);

    // POST /api/usuarios/registro/cliente
    @POST("api/usuarios/registro/cliente")
    Call<Usuario> registrarCliente(@Body Usuario usuario);

    // POST /api/usuarios/registro/taller
    //Pasamos un Map con Object porque tenemos que pasar tanto un usuario como un taller para registrar
    @POST("api/usuarios/registro/taller")
    Call<Usuario> registrarAdminTaller(@Body Map<String, Object> request);

    // GET /api/talleres/cercanos
    @GET("api/talleres/cercanos")
    Call<List<Taller>> getTalleresCercanos(
            @Query("lat") double lat,
            @Query("lng") double lng,
            @Query("radio") double radio
    );

    // GET /api/citas/usuario/{usuarioId}
    @GET("api/citas/usuario/{usuarioId}")
    Call<List<Cita>> getCitasUsuario(@Path("usuarioId") Long usuarioId);

    // GET /api/talleres/{id}
    @GET("api/talleres/{id}")
    Call<Taller> getTallerById(@Path("id") Long id);

    // GET /api/servicios/taller/{tallerId}
    @GET("api/servicios/taller/{tallerId}")
    Call<List<Servicio>> getServiciosByTaller(@Path("tallerId") Long tallerId);

    // GET /api/resenas/taller/{tallerId}
    @GET("api/resenas/taller/{tallerId}")
    Call<List<Resena>> getResenasByTaller(@Path("tallerId") Long tallerId);

    // GET /api/disponibilidad/taller/{tallerId}/horas-libres?fecha=2025-06-10
    @GET("api/disponibilidad/taller/{tallerId}/horas-libres")
    Call<List<String>> getHorasLibres(
            @Path("tallerId") Long tallerId,
            @Query("fecha") String fecha
    );

    // GET /api/vehiculos/usuario/{usuarioId}
    @GET("api/vehiculos/usuario/{usuarioId}")
    Call<List<Vehiculo>> getVehiculosUsuario(@Path("usuarioId") Long usuarioId);

    // POST /api/citas
    @POST("api/citas")
    Call<Cita> crearCita(
            @Query("usuarioId") Long usuarioId,
            @Query("tallerId") Long tallerId,
            @Query("servicioId") Long servicioId,
            @Query("vehiculoId") Long vehiculoId,
            @Body Cita cita
    );
    // PATCH /api/citas/{id}/estado
    @PATCH("api/citas/{id}/estado")
    Call<Cita> actualizarEstadoCita(@Path("id") Long id, @Query("estado") String estado);

    @DELETE("api/vehiculos/{id}")
    Call<Void> eliminarVehiculo(@Path("id") Long id);

    @POST("api/vehiculos/usuario/{usuarioId}")
    Call<Vehiculo> crearVehiculo(@Path("usuarioId") Long usuarioId, @Body Vehiculo vehiculo);

    @POST("api/resenas")
    Call<Resena> crearResena(@Query("usuarioId") Long usuarioId, @Query("tallerId") Long tallerId, @Body Resena resena);

    @GET("api/talleres/buscar")
    Call<List<Taller>> buscarTalleres(@Query("nombre") String nombre);
}