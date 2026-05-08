package com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "paquetes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaqueteEntity {

    @Id
    @Column(name = "id_paquete")
    private UUID idPaquete;

    @Column(name = "id_ruta", nullable = false)
    private UUID idRuta;

    @Column(name = "estado_actual", length = 50)
    private String estadoActual;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
