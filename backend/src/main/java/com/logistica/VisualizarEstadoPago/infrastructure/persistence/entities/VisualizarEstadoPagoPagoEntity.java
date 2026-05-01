package com.logistica.VisualizarEstadoPago.infrastructure.persistence.entities;

import com.logistica.VisualizarEstadoPago.domain.enums.VisualizarEstadoPagoEstadoPagoEnum;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos")
public class VisualizarEstadoPagoPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID usuarioId;
    private BigDecimal montoBase;
    private LocalDateTime fecha;
    private UUID penalidadId;
    private BigDecimal montoNeto;
    private UUID liquidacionId;

    @Enumerated(EnumType.STRING)
    private VisualizarEstadoPagoEstadoPagoEnum estado;

    public VisualizarEstadoPagoPagoEntity() {
    }

    public VisualizarEstadoPagoPagoEntity(UUID id, UUID usuarioId, BigDecimal montoBase, LocalDateTime fecha, UUID penalidadId, BigDecimal montoNeto, UUID liquidacionId, VisualizarEstadoPagoEstadoPagoEnum estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.montoBase = montoBase;
        this.fecha = fecha;
        this.penalidadId = penalidadId;
        this.montoNeto = montoNeto;
        this.liquidacionId = liquidacionId;
        this.estado = estado;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getMontoBase() {
        return montoBase;
    }

    public void setMontoBase(BigDecimal montoBase) {
        this.montoBase = montoBase;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public UUID getPenalidadId() {
        return penalidadId;
    }

    public void setPenalidadId(UUID penalidadId) {
        this.penalidadId = penalidadId;
    }

    public BigDecimal getMontoNeto() {
        return montoNeto;
    }

    public void setMontoNeto(BigDecimal montoNeto) {
        this.montoNeto = montoNeto;
    }

    public UUID getLiquidacionId() {
        return liquidacionId;
    }

    public void setLiquidacionId(UUID liquidacionId) {
        this.liquidacionId = liquidacionId;
    }

    public VisualizarEstadoPagoEstadoPagoEnum getEstado() {
        return estado;
    }

    public void setEstado(VisualizarEstadoPagoEstadoPagoEnum estado) {
        this.estado = estado;
    }
}
