package com.logistica.domain.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AuditoriaLiquidacion {

    private UUID id;
    private UUID idLiquidacion;
    private String operacion;
    private BigDecimal valorAnterior;
    private BigDecimal valorNuevo;
    private OffsetDateTime fechaOperacion;
    private String responsable;

    public AuditoriaLiquidacion(UUID id, UUID idLiquidacion, String operacion, BigDecimal valorAnterior, BigDecimal valorNuevo, OffsetDateTime fechaOperacion, String responsable) {
        this.id = id;
        this.idLiquidacion = idLiquidacion;
        this.operacion = operacion;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.fechaOperacion = fechaOperacion;
        this.responsable = responsable;
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

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public BigDecimal getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(BigDecimal valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public BigDecimal getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(BigDecimal valorNuevo) {
        this.valorNuevo = valorNuevo;
    }

    public OffsetDateTime getFechaOperacion() {
        return fechaOperacion;
    }

    public void setFechaOperacion(OffsetDateTime fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
}
