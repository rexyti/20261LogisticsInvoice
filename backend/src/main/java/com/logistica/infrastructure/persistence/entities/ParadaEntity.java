package com.logistica.infrastructure.persistence.entities;

import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.MotivoFalla;
import com.logistica.domain.enums.ResponsableFalla;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "parada",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parada_id", "ruta_entity_id"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParadaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // ID tecnico

    @Column(name = "parada_id", nullable = false)
    private UUID paradaId; // ID negocio

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_entity_id", nullable = false)
    private RutaEntity ruta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoParada estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_falla")
    private MotivoFalla motivoFalla;

    @Enumerated(EnumType.STRING)
    @Column
    private ResponsableFalla responsable;
}
