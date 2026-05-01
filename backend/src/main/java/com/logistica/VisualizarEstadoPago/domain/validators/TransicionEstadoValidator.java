package com.logistica.VisualizarEstadoPago.domain.validators;

import com.logistica.VisualizarEstadoPago.domain.enums.EstadoPagoEnum;
import org.springframework.stereotype.Component;

@Component
public class TransicionEstadoValidator {

    public boolean isValid(EstadoPagoEnum estadoActual, EstadoPagoEnum nuevoEstado) {
        if (estadoActual == null) {
            return true;
        }
        switch (estadoActual) {
            case PENDIENTE:
                return nuevoEstado == EstadoPagoEnum.PAGADO || nuevoEstado == EstadoPagoEnum.RECHAZADO;
            case PAGADO:
            case RECHAZADO:
                return false;
            default:
                return false;
        }
    }
}
