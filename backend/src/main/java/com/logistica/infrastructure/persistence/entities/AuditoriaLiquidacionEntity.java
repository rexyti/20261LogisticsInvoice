package com.logistica.infrastructure.persistence.entities;

import com.logistica.domain.enums.TipoOperacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria_liquidacion")
@Getter
@Setter
public class AuditoriaLiquidacionEntity {

    @Id
    private UUID id;

    @Column(name = "id_liquidacion", nullable = false)
    private UUID idLiquidacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoOperacion operacion;

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
}
