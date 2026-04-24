package com.logistica.domain.models;

import java.util.UUID;

public class EstadoPago {

    private UUID id;
    private UUID pagoId;
    private String estado;

    public EstadoPago() {
    }

    public EstadoPago(UUID id, UUID pagoId, String estado) {
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
