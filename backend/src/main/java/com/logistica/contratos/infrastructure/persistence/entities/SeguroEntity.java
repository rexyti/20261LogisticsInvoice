package com.logistica.contratos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "seguros")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeguroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_seguro")
    private UUID idSeguro;

    @Column(name = "numero_poliza", nullable = false)
    private String numeroPoliza;

    @Column(nullable = false)
    private String estado;
}
