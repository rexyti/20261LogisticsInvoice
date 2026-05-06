package com.logistica.application.registrarEstadoPago.usecases.pago;

import com.logistica.application.registrarEstadoPago.dtos.request.EventoEstadoPagoRequestDTO;
import com.logistica.application.registrarEstadoPago.dtos.response.RecepcionEventoPagoResponseDTO;

public interface RecibirEventoPagoUseCase {
    RecepcionEventoPagoResponseDTO recibirEvento(EventoEstadoPagoRequestDTO dto);
}
