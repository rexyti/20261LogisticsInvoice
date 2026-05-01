package com.logistica.VisualizarLiquidación.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rutas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisualizarLiquidacionRutaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "tipo_vehiculo", length = 50)
    private String tipoVehiculo;

    @Column(name = "precio_parada", precision = 12, scale = 2)
    private BigDecimal precioParada;

    @Column(name = "numero_paradas")
    private Integer numeroParadas;
}
