package com.logistica.application.contratos.dtos.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContratoCreadoEvent {

    private String tipoEvento;

    private String idContrato;
    private String tipoContrato;

    private TransportistaEventDTO transportista;

    private String tipoVehiculo;

    private Boolean esPorParada;
    private BigDecimal precioParadas;
    private BigDecimal precio;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;

    private SeguroEventDTO seguro;

}
