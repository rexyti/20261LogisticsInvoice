package com.logistica.infrastructure.persistence.entities;

import com.logistica.domain.enums.EstadoPaquete;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "paquetes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaqueteEntity {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID idPaquete;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID idRuta;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EstadoPaquete estadoActual;
}
