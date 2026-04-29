package com.logistica.liquidacion.infrastructure.persistence.mapper;

import com.logistica.liquidacion.application.dtos.request.CierreRutaEventDTO;
import com.logistica.liquidacion.domain.models.Paquete;
import com.logistica.liquidacion.domain.models.LiquidacionRuta;
import org.springframework.stereotype.Component;

@Component
public class LiquidacionRutaMapper {

    public LiquidacionRuta toModel(CierreRutaEventDTO event) {
        return LiquidacionRuta.builder()
                .id(event.getIdRuta())
                .fechaInicio(event.getFechaInicio())
                .fechaCierre(event.getFechaCierre())
                .paquetes(event.getPaquetes().stream()
                        .map(p -> Paquete.builder()
                                .id(p.getId())
                                .estadoFinal(p.getEstadoFinal())
                                .novedades(p.getNovedades())
                                .build())
                        .toList())
                .build();
    }
}
