package com.logistica.infrastructure.visualizarLiquidacion.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ajustes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisualizarLiquidacionAjusteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_liquidacion", nullable = false)
    private VisualizarLiquidacionEntity liquidacion;

    @Column(name = "tipo", nullable = false, length = 100)
    private String tipo;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "razon", length = 500)
    private String razon;
}
