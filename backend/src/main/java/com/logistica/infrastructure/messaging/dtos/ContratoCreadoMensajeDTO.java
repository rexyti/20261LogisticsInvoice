package com.logistica.infrastructure.messaging.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContratoCreadoMensajeDTO {
    private String tipoEvento;
    private String idContrato;
    private String tipoContrato;
    private TransportistaMensajeDTO transportista;
    private String tipoVehiculo;
    private Boolean esPorParada;
    private BigDecimal precioParadas;
    private BigDecimal precio;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;
    private SeguroMensajeDTO seguro;
}
