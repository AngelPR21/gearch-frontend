package com.example.gearch_frontend.api.models;

import com.example.gearch_frontend.api.models.enums.DiaSemana;

// Modelo que representa el horario de un taller para un dia de la semana
// Se usa en HorarioAdminActivity y AnadirHorarioActivity
public class DisponibilidadTaller {
    private Long id;
    private DiaSemana diaSemana;

    // El backend devuelve las horas como String "HH:mm:ss"
    private String horaInicio;
    private String horaFin;
    private Integer intervaloMinutos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public Integer getIntervaloMinutos() { return intervaloMinutos; }
    public void setIntervaloMinutos(Integer intervaloMinutos) { this.intervaloMinutos = intervaloMinutos; }
}
