package com.logistica.VisualizarEstadoPago.domain.events;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class PagoProcesadoEvent extends ApplicationEvent {

    private UUID pagoId;

    public PagoProcesadoEvent(Object source, UUID pagoId) {
        super(source);
        this.pagoId = pagoId;
    }

    public UUID getPagoId() {
        return pagoId;
    }
}
