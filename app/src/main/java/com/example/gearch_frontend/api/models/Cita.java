package com.example.gearch_frontend.api.models;

import com.example.gearch_frontend.api.models.enums.EstadoCita;

public class Cita {
    private Long id;

    //No se puede usar LocalDateTime porque Gson no lo convierte automatico, tocara hacerlo manual
    private String fechaHora;
    private EstadoCita estado;
    private String notas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}