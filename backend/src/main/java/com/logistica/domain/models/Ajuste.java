package com.logistica.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

public class Ajuste {

    private UUID id;
    private UUID idLiquidacion;
    private String tipo;
    private BigDecimal monto;
    private String motivo;

    public Ajuste(UUID id, UUID idLiquidacion, String tipo, BigDecimal monto, String motivo) {
        this.id = id;
        this.idLiquidacion = idLiquidacion;
        this.tipo = tipo;
        this.monto = monto;
        this.motivo = motivo;
    }

    // Getters y Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdLiquidacion() {
        return idLiquidacion;
    }

    public void setIdLiquidacion(UUID idLiquidacion) {
        this.idLiquidacion = idLiquidacion;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
