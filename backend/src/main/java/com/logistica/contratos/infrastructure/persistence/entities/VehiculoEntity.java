package com.logistica.contratos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "vehiculos")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_vehiculo")
    private UUID idVehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportista")
    private TransportistaEntity transportista;

    @Column(nullable = false)
    private String tipo;
}
