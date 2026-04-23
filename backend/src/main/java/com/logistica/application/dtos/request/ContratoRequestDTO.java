package com.logistica.application.dtos.request;

import com.logistica.domain.enums.TipoContrato;
import com.logistica.domain.validators.ValidFechasContrato;
import com.logistica.domain.validators.ValidPrecioCondicional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@ValidFechasContrato
@ValidPrecioCondicional
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContratoRequestDTO {

    @NotBlank(message = "El identificador del contrato es obligatorio")
    private String idContrato;

    @NotNull(message = "El tipo de contrato es obligatorio")
    private TipoContrato tipoContrato;

    @NotBlank(message = "El nombre del conductor es obligatorio")
    private String nombreConductor;

    private BigDecimal precioParadas;

    private BigDecimal precio;

    @NotBlank(message = "El tipo de vehículo es obligatorio")
    private String tipoVehiculo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha final es obligatoria")
    private LocalDate fechaFinal;

    @NotBlank(message = "El estado del seguro es obligatorio")
    private String estadoSeguro;
}
