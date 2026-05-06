package com.logistica.application.novedadEstadoPaquete.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LogSincronizacionDTO {

    private Long id;
    private Long idPaquete;
    private Integer codigoRespuestaHTTP;
    private String jsonRecibido;
    private LocalDateTime fechaSincronizacion;

}