package com.logistica.application.novedadEstadoPaquete.dtos.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NovedadEstadoPaqueteEvent {
    private UUID idPaquete;
    private UUID idRuta;
    private String estado;
}
