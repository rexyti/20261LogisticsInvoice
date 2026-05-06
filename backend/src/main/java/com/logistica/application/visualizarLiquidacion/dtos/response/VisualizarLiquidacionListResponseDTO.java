package com.logistica.application.visualizarLiquidacion.dtos.response;

import java.util.List;

public record VisualizarLiquidacionListResponseDTO(
        List<VisualizarLiquidacionListItemDTO> contenido,
        int pagina,
        int tamano,
        long totalElementos,
        int totalPaginas,
        boolean esUltima
) {}
