package com.logistica.application.ports;

import com.logistica.application.dtos.request.EventoEstadoPagoRequestDTO;

public interface EventoPagoAsyncPort {
    void procesarAsync(EventoEstadoPagoRequestDTO dto);
}
