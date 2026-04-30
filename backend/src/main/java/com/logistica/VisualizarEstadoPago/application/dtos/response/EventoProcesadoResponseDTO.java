package com.logistica.VisualizarEstadoPago.application.dtos.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventoProcesadoResponseDTO {

    private UUID eventoId;
    private String estado;
    private LocalDateTime fechaProcesamiento;

    public EventoProcesadoResponseDTO() {
    }

    public EventoProcesadoResponseDTO(UUID eventoId, String estado, LocalDateTime fechaProcesamiento) {
        this.eventoId = eventoId;
        this.estado = estado;
        this.fechaProcesamiento = fechaProcesamiento;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public void setEventoId(UUID eventoId) {
        this.eventoId = eventoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }
}
