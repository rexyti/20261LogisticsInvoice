package com.logistica.application.contratos.dtos.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistica.domain.shared.enums.TipoVehiculo;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContratoResponseDTO {
    private UUID id;
    private String idContrato;
    private String tipoContrato;
    private TransportistaResponseDTO transportista;
    private TipoVehiculo tipoVehiculo;
    private Boolean esPorParada;
    private BigDecimal precioParadas;
    private BigDecimal precio;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;
    private SeguroResponseDTO seguro;
}