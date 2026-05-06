package com.logistica.infrastructure.visualizarEstadoPago.persistence.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "estados_pago")
public class VisualizarEstadoPagoEstadoPagoEntity {

    @Id
    @Column(name = "id_estado_pago")
    private UUID idEstadoPago;

    private UUID pagoId;
    private String estado;

    public VisualizarEstadoPagoEstadoPagoEntity() {
    }

    public VisualizarEstadoPagoEstadoPagoEntity(UUID idEstadoPago, UUID pagoId, String estado) {
        this.idEstadoPago = idEstadoPago;
        this.pagoId = pagoId;
        this.estado = estado;
    }

    public UUID getIdEstadoPago() {
        return idEstadoPago;
    }

    public void setIdEstadoPago(UUID idEstadoPago) {
        this.idEstadoPago = idEstadoPago;
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
