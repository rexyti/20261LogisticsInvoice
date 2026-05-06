package com.logistica.application.registrarEstadoPago.usecases.pago;

import com.logistica.application.registrarEstadoPago.dtos.response.EventoTransaccionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ObtenerEventosTransaccionUseCase {
    List<EventoTransaccionResponseDTO> obtenerEventos(UUID idPago);
}
