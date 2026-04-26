package com.logistica.application.usecases.pago;

import com.logistica.application.dtos.response.EventoTransaccionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ObtenerEventosTransaccionUseCase {
    List<EventoTransaccionResponseDTO> obtenerEventos(UUID idPago);
}
