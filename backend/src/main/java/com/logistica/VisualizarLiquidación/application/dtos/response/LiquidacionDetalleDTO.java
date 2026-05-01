package com.logistica.VisualizarLiquidación.application.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LiquidacionDetalleDTO(
        UUID idLiquidacion,
        UUID idContrato,
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
        String usuarioId,
        List<AjusteLiquidacionDTO> ajustes
) {}
