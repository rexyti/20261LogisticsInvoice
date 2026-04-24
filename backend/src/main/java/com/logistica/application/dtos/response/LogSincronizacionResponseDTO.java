package com.logistica.application.dtos.response;

import java.time.Instant;
import java.util.UUID;

public record LogSincronizacionResponseDTO(
        UUID id,
        UUID idPaquete,
        int codigoRespuestaHTTP,
        String jsonRecibido,
        Instant timestamp
) {}
