package com.logistica.liquidacion.domain.models;

import lombok.*;

import com.logistica.liquidacion.domain.enums.EstadoLiquidacion;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class Liquidacion {

    private final UUID id;
    private final UUID idRuta;
    private final UUID idContrato;
    private EstadoLiquidacion estado;
    private BigDecimal valorBase;
    private BigDecimal valorFinal;
    private final OffsetDateTime fechaCalculo;
    @Builder.Default
    private List<Ajuste> ajustes = new ArrayList<>();
    @Builder.Default
    private boolean solicitudRevisionAceptada = false;
    
    private final UUID idAdminRevisor;
    private final OffsetDateTime fechaAceptacionRevision;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public static Liquidacion crear(UUID idRuta, UUID idContrato, BigDecimal valorBase) {
        return Liquidacion.builder()
                .id(UUID.randomUUID())
                .idRuta(idRuta)
                .idContrato(idContrato)
                .valorBase(valorBase)
                .valorFinal(valorBase)
                .estado(EstadoLiquidacion.CALCULADA)
                .fechaCalculo(OffsetDateTime.now())
                .ajustes(new ArrayList<>())
                .solicitudRevisionAceptada(false)
                .build();
    }

    public void aplicarAjustes() {
        BigDecimal totalAjustes = ajustes.stream()
                .map(ajuste -> ajuste.getTipo().esBono()
                        ? ajuste.getMonto()
                        : ajuste.getMonto().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.valorFinal = this.valorBase.add(totalAjustes);
    }

    public void aceptarRevision() {
        this.solicitudRevisionAceptada = true;
    }

    public void recalcular(BigDecimal nuevoBase, List<Ajuste> nuevosAjustes) {
        if (!solicitudRevisionAceptada) {
            throw new IllegalStateException("No se puede recalcular sin solicitud aprobada");
        }

        this.valorBase = nuevoBase;
        this.ajustes.clear();
        if (nuevosAjustes != null){
            this.ajustes.addAll(nuevosAjustes);
        }

        aplicarAjustes();

        this.estado = EstadoLiquidacion.RECALCULADA;
    }
}
