package com.logistica.liquidacion.infrastructure.persistence.entities;

import com.logistica.liquidacion.domain.enums.TipoOperacion;
import com.logistica.liquidacion.domain.enums.TipoResponsable;
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
public class AuditoriaLiquidacionEntity  extends InmutableBaseEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_responsable", nullable = false, length = 50)
    private TipoResponsable tipoResponsable;

    @Column(name = "id_responsable", nullable = false)
    private String idResponsable;

}