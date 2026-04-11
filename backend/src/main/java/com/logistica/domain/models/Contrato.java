package com.logistica.domain.models;

import java.util.UUID;
import java.math.BigDecimal;

public class Contrato {
    private UUID id;
    private String tipoContratacion;
    private BigDecimal tarifa; // Puede ser por parada o por ruta completa

    // Constructor, getters, and setters

    public Contrato(UUID id, String tipoContratacion, BigDecimal tarifa) {
        this.id = id;
        this.tipoContratacion = tipoContratacion;
        this.tarifa = tarifa;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTipoContratacion() {
        return tipoContratacion;
    }

    public void setTipoContratacion(String tipoContratacion) {
        this.tipoContratacion = tipoContratacion;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(BigDecimal tarifa) {
        this.tarifa = tarifa;
    }
}
