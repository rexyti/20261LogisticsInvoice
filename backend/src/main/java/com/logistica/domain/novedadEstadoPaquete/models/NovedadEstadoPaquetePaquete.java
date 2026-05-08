package com.logistica.domain.novedadEstadoPaquete.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovedadEstadoPaquetePaquete {

    private UUID idPaquete;
    private UUID idRuta;
    private String estadoActual;
    private LocalDateTime updatedAt;
}
