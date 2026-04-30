package com.logistica.RegistrarEstadoPago.application.ports;

import com.logistica.RegistrarEstadoPago.application.dtos.request.EventoEstadoPagoRequestDTO;

public interface EventoPagoAsyncPort {
    void procesarAsync(EventoEstadoPagoRequestDTO dto);
}
