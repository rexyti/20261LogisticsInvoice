package com.logistica.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria_liquidacion")
public class AuditoriaLiquidacionEntity {

    @Id
    private UUID id;

    @Column(name = "id_liquidacion", nullable = false)
    private UUID idLiquidacion;

    @Column(nullable = false, length = 50)
    private String operacion;

    @Column(name = "valor_anterior", precision = 19, scale = 4)
    private BigDecimal valorAnterior;

    @Column(name = "valor_nuevo", nullable = false, precision = 19, scale = 4)
    private BigDecimal valorNuevo;

    @Column(name = "fecha_operacion", nullable = false)
    private OffsetDateTime fechaOperacion;

    @Column(nullable = false, length = 100)
    private String responsable;
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
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
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
