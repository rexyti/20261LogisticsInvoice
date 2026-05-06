package com.logistica.application.visualizarLiquidacion.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record VisualizarLiquidacionListItemDTO(
        UUID idLiquidacion,
        UUID idRuta,
        LocalDateTime fechaInicio,
        LocalDateTime fechaCierre,
        String tipoVehiculo,
        BigDecimal precioParada,
        Integer numeroParadas,
        BigDecimal montoBruto,
        BigDecimal montoNeto,
        String estadoLiquidacion,
        LocalDateTime fechaCalculo,
        List<VisualizarLiquidacionAjusteDTO> ajustes
) {}
