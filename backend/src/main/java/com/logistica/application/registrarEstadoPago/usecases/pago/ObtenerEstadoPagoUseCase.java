package com.logistica.application.registrarEstadoPago.usecases.pago;

import com.logistica.application.registrarEstadoPago.dtos.response.PagoResponseDTO;

import java.util.UUID;

public interface ObtenerEstadoPagoUseCase {
    PagoResponseDTO obtenerEstadoPago(UUID idPago);
    PagoResponseDTO obtenerEstadoPagoPorLiquidacion(UUID idLiquidacion);
}
