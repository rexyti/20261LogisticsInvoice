package com.logistica.application.visualizarLiquidacion.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VisualizarLiquidacionDetalleDTO(
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
        List<VisualizarLiquidacionAjusteDTO> ajustes
) {}
