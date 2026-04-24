package com.logistica.liquidacion.infrastructure.persistence.entities;

import com.logistica.liquidacion.domain.enums.EstadoLiquidacion;
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
public class LiquidacionEntity extends BaseEntity {


    @Column(name = "id_ruta", nullable = false, unique = true)
    private UUID idRuta;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrato", nullable = false)
    private ContratoEntity contrato;

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
    private boolean solicitudRevisionAceptada = false;

    @Column(name = "id_admin_revisor")
    private UUID idAdminRevisor;

    @Column(name = "fecha_aceptacion_revision")
    private OffsetDateTime fechaAceptacionRevision;

    @Version
    private Long version;

    @OneToMany(mappedBy = "liquidacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AjusteEntity> ajustes;


}
