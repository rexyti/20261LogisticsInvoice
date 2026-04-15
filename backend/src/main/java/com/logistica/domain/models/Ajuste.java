package com.logistica.domain.models;

import com.logistica.domain.enums.TipoAjuste;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class Ajuste {

    private final UUID id;
    private final UUID idLiquidacion;
    private final TipoAjuste tipo;
    private final BigDecimal monto;
    private final String motivo;

    public static class AjusteBuilder {
        public Ajuste build() {

            if (id == null) {
                throw new IllegalArgumentException("El id del ajuste no puede ser null");
            }

            if (idLiquidacion == null) {
                throw new IllegalArgumentException("El id de la liquidación es obligatorio");
            }

            if (tipo == null) {
                throw new IllegalArgumentException("El tipo de ajuste es obligatorio");
            }

            if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto debe ser mayor a 0");
            }

            if (motivo == null || motivo.isBlank()) {
                throw new IllegalArgumentException("El motivo es obligatorio");
            }

            return new Ajuste(id, idLiquidacion, tipo, monto, motivo);
        }
    }

    private Ajuste(UUID id, UUID idLiquidacion, TipoAjuste tipo, BigDecimal monto, String motivo) {
        this.id = id;
        this.idLiquidacion = idLiquidacion;
        this.tipo = tipo;
        this.monto = monto;
        this.motivo = motivo;
    }

    public BigDecimal aplicar(BigDecimal base) {
        if (base == null) {
            throw new IllegalArgumentException("La base no puede ser null");
        }

        if (tipo.esBono()) {
            return base.add(monto);
        } else {
            return base.subtract(monto);
        }
    }
}
