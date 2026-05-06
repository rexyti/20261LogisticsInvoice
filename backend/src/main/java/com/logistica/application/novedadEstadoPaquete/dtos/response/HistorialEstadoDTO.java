package com.logistica.application.novedadEstadoPaquete.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HistorialEstadoDTO(
        Long          id,
        Long          idPaquete,
        String        estado,
        LocalDateTime fecha
) {}
