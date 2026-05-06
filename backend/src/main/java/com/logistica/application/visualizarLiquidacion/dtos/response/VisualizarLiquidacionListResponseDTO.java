package com.logistica.application.visualizarLiquidacion.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record VisualizarLiquidacionListResponseDTO(
        List<VisualizarLiquidacionListItemDTO> contenido,
        int pagina,
        int tamano,
        long totalElementos,
        int totalPaginas,
        boolean esUltima
) {}
