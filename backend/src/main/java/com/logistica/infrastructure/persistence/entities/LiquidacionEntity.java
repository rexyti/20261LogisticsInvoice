package com.logistica.infrastructure.persistence.entities;

import com.logistica.domain.enums.EstadoLiquidacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "liquidaciones")
@Getter
@Setter
public class LiquidacionEntity {

    @Id
    private UUID id;

    @Column(name = "id_ruta", nullable = false, unique = true)
    private UUID idRuta;

    @Column(name = "id_contrato", nullable = false)
    private UUID idContrato;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoLiquidacion estado;

    @Column(name = "valor_base", nullable = false, precision = 19, scale = 4)
    private BigDecimal valorBase;

    @Column(name = "valor_final", nullable = false, precision = 19, scale = 4)
    private BigDecimal valorFinal;

    @Column(name = "fecha_calculo", nullable = false)
    private OffsetDateTime fechaCalculo;

    @Column(name = "solicitud_revision_aceptada", nullable = false)
    private boolean solicitudRevisionAceptada;

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
}
