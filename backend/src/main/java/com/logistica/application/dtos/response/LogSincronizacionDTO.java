package com.logistica.application.dtos.response;

import java.time.LocalDateTime;

public record LogSincronizacionDTO(
        Long          id,
        Long          idPaquete,
        Integer       codigoRespuestaHTTP,
        String        jsonRecibido,
        LocalDateTime createdAt
) {}
