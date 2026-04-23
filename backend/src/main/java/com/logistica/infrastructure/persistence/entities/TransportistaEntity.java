package com.logistica.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "transportista")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportistaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "conductor_id", nullable = false, unique = true)
    private UUID conductorId;

    @Column(nullable = false)
    private String nombre;
}
