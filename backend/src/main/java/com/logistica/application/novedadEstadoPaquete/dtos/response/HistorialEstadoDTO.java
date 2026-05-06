package com.logistica.application.novedadEstadoPaquete.dtos.response;

import java.time.LocalDateTime;

public record HistorialEstadoDTO(
        Long          id,
        Long          idPaquete,
        String        estado,
        LocalDateTime fecha
) {}
