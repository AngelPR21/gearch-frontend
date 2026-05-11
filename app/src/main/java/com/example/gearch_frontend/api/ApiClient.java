package com.example.gearch_frontend.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Clase que crea y gestiona la instancia de Retrofit
// Usa el patron Singleton para que solo exista una instancia en toda la app
public class ApiClient {

    // 10.0.2.2 es el alias del localhost cuando se usa el emulador de Android
    // Para un movil fisico habria que usar la IP de la maquina en la red local
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit = null;

    // Devuelve la instancia de Retrofit, creandola si no existe
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Gson convierte JSON a objetos Java
                    .build();
        }
        return retrofit;
    }
}
