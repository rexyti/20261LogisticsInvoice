package com.logistica.domain.models;

import java.time.Instant;
import java.util.UUID;

public record LogSincronizacion(
        UUID id,
        UUID idPaquete,
        int codigoRespuestaHTTP,
        String jsonRecibido,
        Instant timestamp
) {}
