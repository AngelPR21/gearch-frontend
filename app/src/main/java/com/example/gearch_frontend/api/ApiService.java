package com.example.gearch_frontend.api;

import com.example.gearch_frontend.api.models.Usuario;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
}