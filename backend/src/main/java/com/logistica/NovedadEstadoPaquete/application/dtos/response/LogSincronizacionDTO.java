package com.logistica.NovedadEstadoPaquete.application.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LogSincronizacionDTO {

    private Long id;
    private Long idPaquete;
    private Integer codigoRespuestaHTTP;
    private String jsonRecibido;
    private LocalDateTime fechaSincronizacion;

}