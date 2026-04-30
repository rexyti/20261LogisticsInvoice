package com.logistica.RegistrarEstadoPago.application.usecases.pago;

import com.logistica.RegistrarEstadoPago.application.dtos.response.PagoResponseDTO;

import java.util.UUID;

public interface ObtenerEstadoPagoUseCase {
    PagoResponseDTO obtenerEstadoPago(UUID idPago);
    PagoResponseDTO obtenerEstadoPagoPorLiquidacion(UUID idLiquidacion);
}
