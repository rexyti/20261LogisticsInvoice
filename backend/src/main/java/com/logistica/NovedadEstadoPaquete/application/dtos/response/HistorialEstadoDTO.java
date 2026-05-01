package com.logistica.NovedadEstadoPaquete.application.dtos.response;

import java.time.LocalDateTime;

public record HistorialEstadoDTO(
        Long          id,
        Long          idPaquete,
        String        estado,
        LocalDateTime fecha
) {}
