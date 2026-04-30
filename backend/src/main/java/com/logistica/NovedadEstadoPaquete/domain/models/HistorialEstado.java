package com.logistica.NovedadEstadoPaquete.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEstado {

    private Long id;
    private Long idPaquete;
    private String estado;
    private LocalDateTime fecha;
}
