package com.logistica.contratos.application.dtos.request;

import com.logistica.contratos.application.validators.ValidFechasContrato;
import com.logistica.contratos.application.validators.ValidPrecioCondicional;
import com.logistica.contratos.domain.enums.TipoVehiculo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ValidFechasContrato
@ValidPrecioCondicional
public class ContratoRequestDTO {

    @NotBlank(message = "El identificador del contrato es obligatorio")
    private String idContrato;

    @NotNull(message = "El tipo de contrato es obligatorio")
    private String tipoContrato;

    @NotNull(message = "El Id del transportador es obligatorio")
    private UUID transportistaId;

    @NotNull(message = "esPorParada es obligatorio")
    private Boolean esPorParada;

    private BigDecimal precioParadas;

    private BigDecimal precio;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipoVehiculo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha final es obligatoria")
    private LocalDateTime fechaFinal;

    @Valid
    @NotNull(message = "El seguro es obligatorio")
    private SeguroRequestDTO seguro;
}
