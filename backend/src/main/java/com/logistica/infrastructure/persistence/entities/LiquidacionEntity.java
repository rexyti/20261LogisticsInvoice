package com.logistica.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "liquidaciones")
public class LiquidacionEntity {

    @Id
    private UUID id;

    @Column(name = "id_ruta", nullable = false, unique = true)
    private UUID idRuta;

    @Column(name = "id_contrato", nullable = false)
    private UUID idContrato;

    @Column(nullable = false, length = 50)
    private String estado;

    @Column(name = "valor_final", nullable = false, precision = 19, scale = 4)
    private BigDecimal valorFinal;

    @Column(name = "fecha_calculo", nullable = false)
    private OffsetDateTime fechaCalculo;

    @OneToMany(mappedBy = "liquidacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AjusteEntity> ajustes;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
    
    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(UUID idRuta) {
        this.idRuta = idRuta;
    }

    public UUID getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(UUID idContrato) {
        this.idContrato = idContrato;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public OffsetDateTime getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(OffsetDateTime fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public List<AjusteEntity> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjusteEntity> ajustes) {
        this.ajustes = ajustes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
