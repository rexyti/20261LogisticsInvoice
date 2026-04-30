package com.logistica.NovedadEstadoPaquete.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "paquetes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaqueteEntity {

    @Id
    @Column(name = "id_paquete")
    private Long idPaquete;

    @Column(name = "id_ruta", nullable = false)
    private Long idRuta;

    @Column(name = "estado_actual", length = 50)
    private String estadoActual;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
