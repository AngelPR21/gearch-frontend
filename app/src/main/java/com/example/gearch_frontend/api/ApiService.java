package com.example.gearch_frontend.api;

import com.example.gearch_frontend.api.models.Cita;
import com.example.gearch_frontend.api.models.DisponibilidadTaller;
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
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

// Interfaz que define todos los endpoints de la API
// Retrofit genera automaticamente la implementacion a partir de las anotaciones
public interface ApiService {

    // POST /api/usuarios/login
    // Recibe un Map con email y password, devuelve el usuario con sus datos y rol
    @POST("api/usuarios/login")
    Call<Usuario> login(@Body Map<String, String> credenciales);

    // POST /api/usuarios/registro/cliente
    @POST("api/usuarios/registro/cliente")
    Call<Usuario> registrarCliente(@Body Usuario usuario);

    // POST /api/usuarios/registro/taller
    // Pasamos un Map con Object porque tenemos que pasar tanto un usuario como un taller para registrar
    @POST("api/usuarios/registro/taller")
    Call<Usuario> registrarAdminTaller(@Body Map<String, Object> request);

    // GET /api/talleres/cercanos?lat=39.47&lng=-0.37&radio=10
    // Devuelve los talleres dentro del radio indicado en km usando la formula Haversine
    @GET("api/talleres/cercanos")
    Call<List<Taller>> getTalleresCercanos(
            @Query("lat") double lat,
            @Query("lng") double lng,
            @Query("radio") double radio
    );

    // GET /api/citas/usuario/{usuarioId}
    // Devuelve todas las citas de un usuario
    @GET("api/citas/usuario/{usuarioId}")
    Call<List<Cita>> getCitasUsuario(@Path("usuarioId") Long usuarioId);

    // GET /api/talleres/{id}
    // Devuelve los datos de un taller por id
    @GET("api/talleres/{id}")
    Call<Taller> getTallerById(@Path("id") Long id);

    // GET /api/servicios/taller/{tallerId}
    // Devuelve todos los servicios de un taller
    @GET("api/servicios/taller/{tallerId}")
    Call<List<Servicio>> getServiciosByTaller(@Path("tallerId") Long tallerId);

    // GET /api/resenas/taller/{tallerId}
    // Devuelve todas las resenas de un taller
    @GET("api/resenas/taller/{tallerId}")
    Call<List<Resena>> getResenasByTaller(@Path("tallerId") Long tallerId);

    // GET /api/disponibilidad/taller/{tallerId}/horas-libres?fecha=2025-06-10
    // Devuelve las horas disponibles para reservar en una fecha concreta
    @GET("api/disponibilidad/taller/{tallerId}/horas-libres")
    Call<List<String>> getHorasLibres(
            @Path("tallerId") Long tallerId,
            @Query("fecha") String fecha
    );

    // GET /api/vehiculos/usuario/{usuarioId}
    // Devuelve todos los vehiculos de un usuario
    @GET("api/vehiculos/usuario/{usuarioId}")
    Call<List<Vehiculo>> getVehiculosUsuario(@Path("usuarioId") Long usuarioId);

    // POST /api/citas?usuarioId=1&tallerId=1&servicioId=1&vehiculoId=1 (vehiculoId opcional)
    // Crea una nueva cita. Los ids van como @Query y el objeto cita como @Body
    @POST("api/citas")
    Call<Cita> crearCita(
            @Query("usuarioId") Long usuarioId,
            @Query("tallerId") Long tallerId,
            @Query("servicioId") Long servicioId,
            @Query("vehiculoId") Long vehiculoId,
            @Body Cita cita
    );

    // PATCH /api/citas/{id}/estado
    // Cancela una cita desde el cliente, el estado lo pone el backend directamente
    @PATCH("api/citas/{id}/estado")
    Call<Cita> cancelarCita(@Path("id") Long id);

    // DELETE /api/vehiculos/{id}
    // Elimina un vehiculo por id
    @DELETE("api/vehiculos/{id}")
    Call<Void> eliminarVehiculo(@Path("id") Long id);

    // POST /api/vehiculos/usuario/{usuarioId}
    // Crea un nuevo vehiculo para el usuario
    @POST("api/vehiculos/usuario/{usuarioId}")
    Call<Vehiculo> crearVehiculo(@Path("usuarioId") Long usuarioId, @Body Vehiculo vehiculo);

    // POST /api/resenas?usuarioId=1&tallerId=1
    // Crea una nueva resena
    @POST("api/resenas")
    Call<Resena> crearResena(@Query("usuarioId") Long usuarioId, @Query("tallerId") Long tallerId, @Body Resena resena);

    // GET /api/talleres/buscar?nombre=AutoTop
    // Busca talleres por nombre ignorando mayusculas
    @GET("api/talleres/buscar")
    Call<List<Taller>> buscarTalleres(@Query("nombre") String nombre);

    // GET /api/usuarios/{id}
    // Devuelve los datos de un usuario por id
    @GET("api/usuarios/{id}")
    Call<Usuario> getUsuario(@Path("id") Long id);

    // PUT /api/usuarios/{id}
    // Actualiza el perfil de un usuario
    @PUT("api/usuarios/{id}")
    Call<Usuario> actualizarUsuario(@Path("id") Long id, @Body Usuario usuario);

    // DELETE /api/usuarios/{id}
    // Elimina la cuenta de un usuario y todos sus datos
    @DELETE("api/usuarios/{id}")
    Call<Void> eliminarUsuario(@Path("id") Long id);

    // GET /api/citas/taller/{tallerId}
    // Devuelve todas las citas de un taller
    @GET("api/citas/taller/{tallerId}")
    Call<List<Cita>> getCitasTaller(@Path("tallerId") Long tallerId);

    // POST /api/admin/{adminId}/servicios
    // Crea un nuevo servicio en el taller del admin
    @POST("api/admin/{adminId}/servicios")
    Call<Servicio> crearServicio(@Path("adminId") Long adminId, @Body Servicio servicio);

    // DELETE /api/admin/{adminId}/servicios/{servicioId}
    // Elimina un servicio del taller del admin
    @DELETE("api/admin/{adminId}/servicios/{servicioId}")
    Call<Void> eliminarServicio(@Path("adminId") Long adminId, @Path("servicioId") Long servicioId);

    // GET /api/admin/{adminId}/horario
    // Devuelve el horario semanal del taller del admin
    @GET("api/admin/{adminId}/horario")
    Call<List<DisponibilidadTaller>> getHorario(@Path("adminId") Long adminId);

    // POST /api/admin/{adminId}/horario
    // Crea o sobreescribe el horario de un dia de la semana
    @POST("api/admin/{adminId}/horario")
    Call<DisponibilidadTaller> crearHorario(@Path("adminId") Long adminId, @Body DisponibilidadTaller disponibilidad);

    // DELETE /api/admin/{adminId}/horario/{disponibilidadId}
    // Elimina el horario de un dia concreto
    @DELETE("api/admin/{adminId}/horario/{disponibilidadId}")
    Call<Void> eliminarHorario(@Path("adminId") Long adminId, @Path("disponibilidadId") Long disponibilidadId);

    // GET /api/admin/{adminId}/taller
    // Devuelve los datos del taller que administra el admin
    @GET("api/admin/{adminId}/taller")
    Call<Taller> getMiTaller(@Path("adminId") Long adminId);

    // PUT /api/admin/{adminId}/taller
    // Actualiza los datos del taller del admin
    @PUT("api/admin/{adminId}/taller")
    Call<Void> actualizarTaller(@Path("adminId") long adminId, @Body Taller taller);

    // PATCH /api/usuarios/{id}/fcm-token?token=xxxxx
    // Envia el token de Firebase al backend para poder recibir notificaciones push
    @PATCH("api/usuarios/{id}/fcm-token")
    Call<Void> actualizarFcmToken(@Path("id") Long id, @Query("token") String token);

    // PATCH /api/admin/{adminId}/citas/{citaId}/estado?estado=CANCELADA
    // El admin cambia el estado de una cita y el backend notifica al cliente
    @PATCH("api/admin/{adminId}/citas/{citaId}/estado")
    Call<Cita> cambiarEstadoCitaAdmin(@Path("adminId") long adminId, @Path("citaId") Long citaId, @Query("estado") String estado);
}