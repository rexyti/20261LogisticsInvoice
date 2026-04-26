package com.logistica.application.dtos.response;

import com.logistica.domain.enums.EstadoEventoTransaccion;
import com.logistica.domain.enums.EstadoPagoEnum;

import java.time.Instant;
import java.util.UUID;

public record EventoTransaccionResponseDTO(
        UUID idEvento,
        String idTransaccionBanco,
        UUID idPago,
        UUID idLiquidacion,
        EstadoPagoEnum estadoSolicitado,
        EstadoEventoTransaccion estadoProcesamiento,
        Instant fechaRecepcion,
        Instant fechaEventoBanco,
        Long secuencia,
        String mensajeError
) {}
