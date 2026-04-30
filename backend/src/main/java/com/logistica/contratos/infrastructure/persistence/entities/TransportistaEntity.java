package com.logistica.contratos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transportistas")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportistaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_transportista")
    private UUID idTransportista;

    @Column(nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "transportista")
    @Builder.Default
    private List<VehiculoEntity> vehiculos = new ArrayList<>();
}
