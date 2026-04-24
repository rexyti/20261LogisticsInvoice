package com.logistica.liquidacion.domain.models;

import com.logistica.liquidacion.domain.enums.TipoAjuste;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class Ajuste {

    private final UUID id;
    private final UUID idLiquidacion;
    private final TipoAjuste tipo;
    private final BigDecimal monto;
    private final String motivo;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public static class AjusteBuilder {
        public Ajuste build() {

            if (id == null) {
                throw new IllegalArgumentException("El id del ajuste no puede ser null");
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

            return new Ajuste(id, idLiquidacion, tipo, monto, motivo, createdAt, updatedAt);
        }
    }

    private Ajuste(UUID id, UUID idLiquidacion, TipoAjuste tipo, BigDecimal monto, String motivo, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.idLiquidacion = idLiquidacion;
        this.tipo = tipo;
        this.monto = monto;
        this.motivo = motivo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Ajuste asociarALiquidacion(UUID liquidacionId) {
        if (liquidacionId == null) {
            throw new IllegalArgumentException("El id de la liquidación es obligatorio");
        }

        return Ajuste.builder()
                .id(this.id != null ? this.id : UUID.randomUUID())
                .idLiquidacion(liquidacionId)
                .tipo(this.tipo)
                .monto(this.monto)
                .motivo(this.motivo)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
