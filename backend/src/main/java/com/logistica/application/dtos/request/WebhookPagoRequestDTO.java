package com.logistica.application.dtos.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WebhookPagoRequestDTO {

    private UUID eventoId;
    private UUID pagoId;
    private String nuevoEstado;
    private LocalDateTime fechaEvento;
    private String motivo;
    private BigDecimal monto;

    public WebhookPagoRequestDTO() {
    }

    public WebhookPagoRequestDTO(UUID eventoId, UUID pagoId, String nuevoEstado, LocalDateTime fechaEvento, String motivo, BigDecimal monto) {
        this.eventoId = eventoId;
        this.pagoId = pagoId;
        this.nuevoEstado = nuevoEstado;
        this.fechaEvento = fechaEvento;
        this.motivo = motivo;
        this.monto = monto;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public void setEventoId(UUID eventoId) {
        this.eventoId = eventoId;
    }

    public UUID getPagoId() {
        return pagoId;
    }

    public void setPagoId(UUID pagoId) {
        this.pagoId = pagoId;
    }

    public String getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(String nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
