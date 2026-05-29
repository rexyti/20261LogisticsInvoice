package com.logistica.infrastructure.registrarEstadoPago.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BancoPagoResponseDTO {

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("voucher")
    private String voucher;

    @JsonProperty("fecha_procesamiento")
    private String fechaProcesamiento;

    @JsonProperty("motivo")
    private String motivo;
}
