package com.example.gearch_frontend.api.models;

import com.example.gearch_frontend.api.models.enums.EstadoCita;

// Modelo que representa una cita en el frontend
// Debe tener los mismos campos que devuelve el backend en el JSON
public class Cita {
    private Long id;
    private Servicio servicio;

    // No se puede usar LocalDateTime porque Gson no lo convierte automaticamente
    // Se usa String con formato "yyyy-MM-ddTHH:mm:ss" que el backend deserializa como LocalDateTime
    private String fechaHora;
    private EstadoCita estado;
    private String notas;
    private Taller taller;
    private Usuario usuario;
    private Vehiculo vehiculo;

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Taller getTaller() { return taller; }
    public void setTaller(Taller taller) { this.taller = taller; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
}
