package com.logistica.liquidacion.infrastructure.persistence.mapper;

import com.logistica.liquidacion.application.dtos.request.LiquidacionCierreRutaEventDTO;
import com.logistica.liquidacion.domain.models.LiquidacionPaquete;
import com.logistica.liquidacion.domain.models.LiquidacionRuta;
import org.springframework.stereotype.Component;

@Component
public class LiquidacionRutaMapper {

    public LiquidacionRuta toModel(LiquidacionCierreRutaEventDTO event) {
        return LiquidacionRuta.builder()
                .id(event.getIdRuta())
                .fechaInicio(event.getFechaInicio())
                .fechaCierre(event.getFechaCierre())
                .paquetes(event.getPaquetes().stream()
                        .map(p -> LiquidacionPaquete.builder()
                                .id(p.getId())
                                .estadoFinal(p.getEstadoFinal())
                                .novedades(p.getNovedades())
                                .build())
                        .toList())
                .build();
    }
}
