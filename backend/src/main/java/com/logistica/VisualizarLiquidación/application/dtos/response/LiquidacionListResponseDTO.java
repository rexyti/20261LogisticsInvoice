package com.logistica.VisualizarLiquidación.application.dtos.response;

import java.util.List;

public record LiquidacionListResponseDTO(
        List<LiquidacionListItemDTO> contenido,
        int pagina,
        int tamano,
        long totalElementos,
        int totalPaginas,
        boolean esUltima
) {}
