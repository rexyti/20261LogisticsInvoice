package com.logistica.application.novedadEstadoPaquete.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HistorialEstadoDTO(
        UUID id,
        UUID          idPaquete,
        String        estado,
        LocalDateTime fecha
) {}
