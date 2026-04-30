package com.logistica.contratos.application.dtos.response;


import com.logistica.contratos.domain.enums.TipoVehiculo;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
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