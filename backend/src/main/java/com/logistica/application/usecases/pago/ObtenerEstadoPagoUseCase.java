package com.logistica.application.usecases.pago;

import com.logistica.application.dtos.response.PagoResponseDTO;

import java.util.UUID;

public interface ObtenerEstadoPagoUseCase {
    PagoResponseDTO obtenerEstadoPago(UUID idPago);
    PagoResponseDTO obtenerEstadoPagoPorLiquidacion(UUID idLiquidacion);
}
