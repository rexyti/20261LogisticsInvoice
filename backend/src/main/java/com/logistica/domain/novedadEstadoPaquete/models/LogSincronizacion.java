package com.logistica.domain.novedadEstadoPaquete.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogSincronizacion {

    private Long id;
    private Long idPaquete;
    private Integer codigoRespuestaHTTP;
    private String jsonRecibido;
    private LocalDateTime createdAt;
}
