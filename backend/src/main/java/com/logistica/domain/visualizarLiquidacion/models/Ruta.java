package com.logistica.domain.visualizarLiquidacion.models;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Ruta {
    private UUID id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCierre;
    private String tipoVehiculo;
    private BigDecimal precioParada;
    private Integer numeroParadas;
}
