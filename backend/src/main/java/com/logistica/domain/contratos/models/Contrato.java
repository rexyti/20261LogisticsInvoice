package com.logistica.domain.contratos.models;

import com.logistica.domain.shared.enums.TipoVehiculo;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Contrato {
    private UUID id;
    private String idContrato;
    private String tipoContrato;
    private Transportista transportista;
    private TipoVehiculo tipoVehiculo;
    private Boolean esPorParada;
    private BigDecimal precioParadas;
    private BigDecimal precio;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;
    private Seguro seguro;
}
