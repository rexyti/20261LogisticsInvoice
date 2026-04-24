package com.logistica.liquidacion.domain.models;

import com.logistica.liquidacion.domain.enums.TipoContratacion;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class Contrato {

    private final UUID id;
    private final TipoContratacion tipoContratacion;
    private final BigDecimal tarifa;

    public static class ContratoBuilder {
        public Contrato build() {
            if (id == null) {
                throw new IllegalArgumentException("El id del contrato no puede ser null");
            }

            if (tipoContratacion == null) {
                throw new IllegalArgumentException("El tipo de contratación es obligatorio");
            }

            if (tarifa == null || tarifa.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("La tarifa debe ser mayor a 0");
            }

            return new Contrato(id, tipoContratacion, tarifa);
        }
    }

    private Contrato(UUID id, TipoContratacion tipoContratacion, BigDecimal tarifa) {
        this.id = id;
        this.tipoContratacion = tipoContratacion;
        this.tarifa = tarifa;
    }

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
