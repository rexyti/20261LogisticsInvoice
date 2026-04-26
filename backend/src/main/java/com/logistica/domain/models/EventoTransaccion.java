package com.logistica.domain.models;

import com.logistica.domain.enums.EstadoEventoTransaccion;
import com.logistica.domain.enums.EstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record EventoTransaccion(
        UUID idEvento,
        String idTransaccionBanco,
        UUID idPago,
        UUID idLiquidacion,
        String payloadRecibido,
        Instant fechaRecepcion,
        Instant fechaEventoBanco,
        Long secuencia,
        EstadoPagoEnum estadoSolicitado,
        EstadoEventoTransaccion estadoProcesamiento,
        String mensajeError,
        boolean procesado
) {
    public EventoTransaccion conEstadoProcesamiento(EstadoEventoTransaccion nuevoEstado, String error) {
        return new EventoTransaccion(idEvento, idTransaccionBanco, idPago, idLiquidacion,
                payloadRecibido, fechaRecepcion, fechaEventoBanco, secuencia,
                estadoSolicitado, nuevoEstado, error, true);
    }
}
