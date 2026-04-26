package com.logistica.application.usecases.pago;

import com.logistica.application.dtos.request.EventoEstadoPagoRequestDTO;

public interface ProcesarEventoPagoUseCase {
    void procesarEvento(EventoEstadoPagoRequestDTO dto);
}
