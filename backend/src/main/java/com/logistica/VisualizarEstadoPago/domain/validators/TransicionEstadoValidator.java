package com.logistica.VisualizarEstadoPago.domain.validators;

import com.logistica.VisualizarEstadoPago.domain.enums.VisualizarEstadoPagoEstadoPagoEnum;
import org.springframework.stereotype.Component;

@Component
public class TransicionEstadoValidator {

    public boolean isValid(VisualizarEstadoPagoEstadoPagoEnum estadoActual, VisualizarEstadoPagoEstadoPagoEnum nuevoEstado) {
        if (estadoActual == null) {
            return true;
        }
        switch (estadoActual) {
            case PENDIENTE:
                return nuevoEstado == VisualizarEstadoPagoEstadoPagoEnum.PAGADO || nuevoEstado == VisualizarEstadoPagoEstadoPagoEnum.RECHAZADO;
            case PAGADO:
            case RECHAZADO:
                return false;
            default:
                return false;
        }
    }
}
