package com.example.gearch_frontend.api.models.enums;

// Estados posibles de una cita
// Las citas se crean siempre como CONFIRMADA desde el backend
// El cliente puede cancelarla y el admin puede cancelarla o completarla
public enum EstadoCita {
    CONFIRMADA,
    CANCELADA,
    COMPLETADA
}
