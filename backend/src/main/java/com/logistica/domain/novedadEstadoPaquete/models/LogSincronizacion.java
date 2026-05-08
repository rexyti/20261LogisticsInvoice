package com.logistica.domain.novedadEstadoPaquete.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogSincronizacion {

    private UUID id;
    private UUID idPaquete;
    private Integer codigoRespuestaHTTP;
    private String jsonRecibido;
    private LocalDateTime createdAt;
}
