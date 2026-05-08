package com.logistica.domain.novedadEstadoPaquete.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEstado {

    private UUID id;
    private UUID idPaquete;
    private String estado;
    private LocalDateTime fecha;
}
