package com.example.gearch_frontend.api.models;

// Modelo que representa una resena de un cliente sobre un taller
public class Resena {
    private Long id;
    private String comentario;
    private Integer puntuacion;

    // La fecha viene como String desde el backend porque Gson no convierte LocalDateTime automaticamente
    private String fecha;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
