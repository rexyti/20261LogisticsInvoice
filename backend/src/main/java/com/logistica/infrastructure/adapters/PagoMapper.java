package com.logistica.infrastructure.adapters;

import com.logistica.application.dtos.response.EstadoPagoResponseDTO;
import com.logistica.domain.models.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public EstadoPagoResponseDTO toEstadoPagoResponseDTO(Pago pago) {
        if (pago == null) {
            return null;
        }
        return new EstadoPagoResponseDTO(
                pago.getId(),
                pago.getEstado().name(),
                pago.getFecha(),
                pago.getMontoNeto(),
                null, // Motivo de rechazo no está en el modelo de dominio
                pago.getLiquidacionId()
        );
    }
}
