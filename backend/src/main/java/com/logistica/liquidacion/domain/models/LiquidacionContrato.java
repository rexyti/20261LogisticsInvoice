package com.logistica.liquidacion.domain.models;

import com.logistica.liquidacion.domain.enums.TipoContratacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class LiquidacionContrato {

    private final UUID id;
    private final TipoContratacion tipoContratacion;
    private final BigDecimal tarifa;

    public BigDecimal getTarifaSegura() {
        if (tarifa == null || tarifa.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("El contrato no tiene una tarifa válida configurada");
        }
        return tarifa;
    }

    public boolean esPorParada() {
        return tipoContratacion == TipoContratacion.POR_PARADA;
    }

    public boolean esRecorridoCompleto() {
        return tipoContratacion == TipoContratacion.RECORRIDO_COMPLETO;
    }
}
