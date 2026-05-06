package com.logistica.application.registrarEstadoPago.ports;

import com.logistica.application.registrarEstadoPago.dtos.request.EventoEstadoPagoRequestDTO;

public interface EventoPagoAsyncPort {
    void procesarAsync(EventoEstadoPagoRequestDTO dto);
}
