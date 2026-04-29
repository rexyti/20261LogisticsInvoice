package com.logistica.cierreRuta.infrastructure.persistence.entities;

import com.logistica.cierreRuta.domain.enums.EstadoParada;
import com.logistica.cierreRuta.domain.enums.MotivoFalla;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "parada",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_parada_parada_id_ruta_entity_id",
                        columnNames = {"parada_id", "ruta_entity_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "paradaId")          // ← basado en ID de negocio
public class ParadaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;                          // ID técnico

    @Column(name = "paquete_id")
    private UUID paqueteId;

    @Column(name = "parada_id", nullable = false)
    private UUID paradaId;                    // ID de negocio

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_entity_id", nullable = false)    //
    private RutaEntity ruta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoParada estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_falla")
    private MotivoFalla motivoFalla;


}