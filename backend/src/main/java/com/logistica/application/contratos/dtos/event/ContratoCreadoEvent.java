package com.logistica.application.contratos.dtos.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContratoCreadoEvent {

    private String tipoEvento;

    private String idContrato;
    private String tipoContrato;

    private UUID transportistaId;
    private String nombreTransportista;

    private String tipoVehiculo;

    private Boolean esPorParada;
    private BigDecimal precioParadas;
    private BigDecimal precio;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;

    private SeguroEventDTO seguro;

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SeguroEventDTO {
        private String numeroPoliza;
        private String estado;
    }
}
