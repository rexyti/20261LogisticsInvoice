package com.logistica.domain.models;

import com.logistica.domain.enums.TipoContrato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contrato {
    private Long id;
    private String idContrato;
    private TipoContrato tipoContrato;
    private String nombreConductor;
    private BigDecimal precioParadas;
    private BigDecimal precio;
    private String tipoVehiculo;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private Long idUsuario;
    private Long idVehiculo;
    private String estadoSeguro;
    private LocalDateTime createdAt;
}
