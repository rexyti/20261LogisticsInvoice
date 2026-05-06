package com.logistica.domain.visualizarEstadoPago.models;

import java.util.UUID;

public class VisualizarEstadoPagoEstadoPago {

    private UUID id;
    private UUID pagoId;
    private String estado;

    public VisualizarEstadoPagoEstadoPago() {
    }

    public VisualizarEstadoPagoEstadoPago(UUID id, UUID pagoId, String estado) {
        this.id = id;
        this.pagoId = pagoId;
        this.estado = estado;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPagoId() {
        return pagoId;
    }

    public void setPagoId(UUID pagoId) {
        this.pagoId = pagoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
