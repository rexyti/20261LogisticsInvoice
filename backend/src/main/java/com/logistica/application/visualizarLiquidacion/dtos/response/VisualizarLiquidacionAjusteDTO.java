package com.logistica.application.visualizarLiquidacion.dtos.response;

import java.math.BigDecimal;
import java.util.UUID;

public record VisualizarLiquidacionAjusteDTO(
        UUID id,
        String tipo,
        BigDecimal monto,
        String razon
) {}
