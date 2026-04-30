package com.logistica.RegistrarEstadoPago.application.usecases.pago;

import com.logistica.RegistrarEstadoPago.application.dtos.request.EventoEstadoPagoRequestDTO;

public interface ProcesarEventoPagoUseCase {
    void procesarEvento(EventoEstadoPagoRequestDTO dto);
}
