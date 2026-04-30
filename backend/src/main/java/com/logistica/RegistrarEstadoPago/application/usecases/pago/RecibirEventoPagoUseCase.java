package com.logistica.RegistrarEstadoPago.application.usecases.pago;

import com.logistica.RegistrarEstadoPago.application.dtos.request.EventoEstadoPagoRequestDTO;
import com.logistica.RegistrarEstadoPago.application.dtos.response.RecepcionEventoPagoResponseDTO;

public interface RecibirEventoPagoUseCase {
    RecepcionEventoPagoResponseDTO recibirEvento(EventoEstadoPagoRequestDTO dto);
}
