package com.logistica.RegistrarEstadoPago.application.usecases.pago;

import com.logistica.RegistrarEstadoPago.application.dtos.response.EventoTransaccionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ObtenerEventosTransaccionUseCase {
    List<EventoTransaccionResponseDTO> obtenerEventos(UUID idPago);
}
