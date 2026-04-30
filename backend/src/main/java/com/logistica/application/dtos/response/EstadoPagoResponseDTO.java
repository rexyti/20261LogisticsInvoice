package com.logistica.application.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class EstadoPagoResponseDTO {

    private UUID pagoId;
    private String estado;
    private LocalDateTime fechaActualizacion;
    private BigDecimal monto;
    private String motivoRechazo;
    private UUID liquidacionId;

    public EstadoPagoResponseDTO() {
    }

    public EstadoPagoResponseDTO(UUID pagoId, String estado, LocalDateTime fechaActualizacion, BigDecimal monto, String motivoRechazo, UUID liquidacionId) {
        this.pagoId = pagoId;
        this.estado = estado;
        this.fechaActualizacion = fechaActualizacion;
        this.monto = monto;
        this.motivoRechazo = motivoRechazo;
        this.liquidacionId = liquidacionId;
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

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public UUID getLiquidacionId() {
        return liquidacionId;
    }

    public void setLiquidacionId(UUID liquidacionId) {
        this.liquidacionId = liquidacionId;
    }
}
