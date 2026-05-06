package com.logistica.application.registrarEstadoPago.usecases.pago;

import com.logistica.application.registrarEstadoPago.dtos.request.EventoEstadoPagoRequestDTO;

public interface ProcesarEventoPagoUseCase {
    void procesarEvento(EventoEstadoPagoRequestDTO dto);
}
