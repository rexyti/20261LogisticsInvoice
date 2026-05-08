package com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_estados",
       indexes = {
           @Index(name = "idx_historial_id_paquete", columnList = "id_paquete"),
           @Index(name = "idx_historial_fecha",      columnList = "fecha")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEstadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "id_paquete", nullable = false)
    private UUID idPaquete;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
