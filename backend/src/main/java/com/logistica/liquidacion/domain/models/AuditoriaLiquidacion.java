package com.logistica.liquidacion.domain.models;

import com.logistica.liquidacion.domain.enums.TipoResponsable;
import lombok.Builder;
import lombok.Getter;
import com.logistica.liquidacion.domain.enums.TipoOperacion;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class AuditoriaLiquidacion {

    private final UUID id;
    private final UUID idLiquidacion;
    private final TipoOperacion operacion;
    private final BigDecimal valorAnterior;
    private final BigDecimal valorNuevo;
    private final OffsetDateTime fechaOperacion;
    private final TipoResponsable tipoResponsable;
    private final String idResponsable;
    private final OffsetDateTime createdAt;


    public static AuditoriaLiquidacion crearCalculo(
            UUID idLiquidacion,
            BigDecimal valorNuevo
    ) {
        if (valorNuevo == null){
            throw new IllegalArgumentException("El valor nuevo no puede ser nulo");
        }
        return AuditoriaLiquidacion.builder()
                .id(UUID.randomUUID())
                .idLiquidacion(idLiquidacion)
                .operacion(TipoOperacion.CALCULO)
                .valorAnterior(null)
                .valorNuevo(valorNuevo)
                .fechaOperacion(OffsetDateTime.now())
                .tipoResponsable(TipoResponsable.SISTEMA)
                .idResponsable("SYSTEM")
                .build();
    }


    public static AuditoriaLiquidacion crearRecalculo(
            UUID idLiquidacion,
            BigDecimal valorAnterior,
            BigDecimal valorNuevo,
            TipoResponsable tipoResponsable,
            String idResponsable
    ) {
        if (idLiquidacion == null) {
            throw new IllegalArgumentException("El id de la liquidación no puede ser null");
        }

        if (valorNuevo == null) {
            throw new IllegalArgumentException("El valor nuevo no puede ser null");
        }

        if (valorAnterior == null) {
            throw new IllegalArgumentException("El valor anterior no puede ser null");
        }

        if (tipoResponsable == null) {
            throw new IllegalArgumentException("El tipo de responsable es obligatorio");
        }

        if (idResponsable == null || idResponsable.isBlank()) {
            throw new IllegalArgumentException("El ID del responsable es obligatorio");
        }

        return AuditoriaLiquidacion.builder()
                .id(UUID.randomUUID())
                .idLiquidacion(idLiquidacion)
                .operacion(TipoOperacion.RECALCULO)
                .valorAnterior(valorAnterior)
                .valorNuevo(valorNuevo)
                .fechaOperacion(OffsetDateTime.now())
                .tipoResponsable(tipoResponsable)
                .idResponsable(idResponsable)
                .build();
    }
}
