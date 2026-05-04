package com.example.gearch_frontend.api;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // POST /api/usuarios/login
    @POST("api/usuarios/login")
    Call<Map<String, Object>> login(@Body Map<String, String> credenciales);
}