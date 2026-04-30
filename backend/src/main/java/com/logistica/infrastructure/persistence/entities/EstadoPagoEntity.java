package com.logistica.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "estados_pago")
public class EstadoPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID pagoId;
    private String estado;

    public EstadoPagoEntity() {
    }

    public EstadoPagoEntity(UUID id, UUID pagoId, String estado) {
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
