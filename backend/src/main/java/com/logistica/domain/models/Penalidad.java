package com.logistica.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

public class Penalidad {

    private UUID id;
    private String tipo;
    private BigDecimal monto;

    public Penalidad() {
    }

    public Penalidad(UUID id, String tipo, BigDecimal monto) {
        this.id = id;
        this.tipo = tipo;
        this.monto = monto;
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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
