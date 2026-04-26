package com.logistica.application.usecases.pago;

import com.logistica.application.dtos.request.EventoEstadoPagoRequestDTO;
import com.logistica.application.dtos.response.RecepcionEventoPagoResponseDTO;

public interface RecibirEventoPagoUseCase {
    RecepcionEventoPagoResponseDTO recibirEvento(EventoEstadoPagoRequestDTO dto);
}
