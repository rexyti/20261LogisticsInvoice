package com.logistica.infrastructure.visualizarLiquidacion.persistence.entities;

import com.logistica.domain.visualizarLiquidacion.enums.EstadoLiquidacion;
import com.logistica.infrastructure.liquidacion.persistence.entities.AjusteEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Immutable
@Entity
@Table(name = "liquidaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisualizarLiquidacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta", nullable = false)
    private VisualizarLiquidacionRutaEntity ruta;

    @Column(name = "id_contrato")
    private UUID idContrato;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_liquidacion", nullable = false, length = 50)
    private EstadoLiquidacion estadoLiquidacion;

    @Column(name = "monto_bruto", precision = 12, scale = 2)
    private BigDecimal montoBruto;

    @Column(name = "monto_neto", precision = 12, scale = 2)
    private BigDecimal montoNeto;

    @Column(name = "fecha_calculo", nullable = false)
    private LocalDateTime fechaCalculo;

    @Column(name = "usuario_id", nullable = false, length = 255)
    private String usuarioId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_liquidacion", insertable = false, updatable = false)
    @Builder.Default
    private List<AjusteEntity> ajustes = new ArrayList<>();
}
