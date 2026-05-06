package com.logistica.application.visualizarLiquidacion.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record VisualizarLiquidacionAjusteDTO(
        UUID id,
        String tipo,
        BigDecimal monto,
        String razon
) {}
