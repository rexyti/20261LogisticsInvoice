package com.logistica.cierreRuta.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "transportista",
        uniqueConstraints = @UniqueConstraint(name = "uk_transportista_conductor_id", columnNames = "conductor_id")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CierreRutaTransportistaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // ID tecnico

    @Column(name = "conductor_id", nullable = false, unique = true)
    private UUID conductorId;

    @Column(nullable = false)
    private String nombre;
}
