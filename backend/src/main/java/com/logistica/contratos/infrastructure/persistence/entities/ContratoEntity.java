package com.logistica.contratos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contratos")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContratoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_contrato", nullable = false, unique = true)
    private String idContrato;

    @Column(name = "tipo_contrato", nullable = false)
    private String tipoContrato;

    @Column(name = "es_por_parada", nullable = false)
    private Boolean esPorParada;

    @Column(name = "precio_paradas")
    private BigDecimal precioParadas;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "tipo_vehiculo", nullable = false)
    private String tipoVehiculo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_final", nullable = false)
    private LocalDateTime fechaFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportista", nullable = false)
    private TransportistaEntity transportista;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seguro")
    private SeguroEntity seguro;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void asignarFechaCreacion() {
        this.createdAt = LocalDateTime.now();
    }
}
