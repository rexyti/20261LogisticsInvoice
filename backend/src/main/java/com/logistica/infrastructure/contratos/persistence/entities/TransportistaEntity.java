package com.logistica.infrastructure.contratos.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transportista")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportistaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "transportista")
    @Builder.Default
    private List<VehiculoEntity> vehiculos = new ArrayList<>();
}
