package com.logistica.infrastructure.visualizarEstadoPago.persistence.entities;

import com.logistica.domain.visualizarEstadoPago.enums.VisualizarEstadoPagoEstadoPagoEnum;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos")
public class VisualizarEstadoPagoPagoEntity {

    @Id
    @Column(name = "id_pago")
    private UUID idPago;

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

    public VisualizarEstadoPagoPagoEntity(UUID idPago, UUID usuarioId, BigDecimal montoBase, LocalDateTime fecha, UUID penalidadId, BigDecimal montoNeto, UUID liquidacionId, VisualizarEstadoPagoEstadoPagoEnum estado) {
        this.idPago = idPago;
        this.usuarioId = usuarioId;
        this.montoBase = montoBase;
        this.fecha = fecha;
        this.penalidadId = penalidadId;
        this.montoNeto = montoNeto;
        this.liquidacionId = liquidacionId;
        this.estado = estado;
    }

    public UUID getIdPago() {
        return idPago;
    }

    public void setIdPago(UUID idPago) {
        this.idPago = idPago;
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
