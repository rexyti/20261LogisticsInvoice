package com.logistica.infrastructure.registrarEstadoPago.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BancoPagoRequestDTO {

    @JsonProperty("id_liquidacion")
    private UUID idLiquidacion;

    @JsonProperty("monto")
    private BigDecimal monto;
}
