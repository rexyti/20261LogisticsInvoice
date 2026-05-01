package com.logistica.VisualizarEstadoPago.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventoTransaccion {

    private UUID id;
    private String tipo;
    private LocalDateTime fecha;
    private String datos;

    public EventoTransaccion() {
    }

    public EventoTransaccion(UUID id, String tipo, LocalDateTime fecha, String datos) {
        this.id = id;
        this.tipo = tipo;
        this.fecha = fecha;
        this.datos = datos;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }
}
