package com.logistica.infrastructure.persistence.entities;

import com.logistica.domain.enums.EstadoPaquete;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "historial_estados",
    indexes = {
        @Index(name = "idx_historial_paquete", columnList = "idPaquete"),
        @Index(name = "idx_historial_fecha",   columnList = "fecha")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEstadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID idPaquete;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoPaquete estado;

    @Column(nullable = false)
    private Instant fecha;
}
