package com.logistica.infrastructure.liquidacion.persistence.mapper;

import com.logistica.application.liquidacion.dtos.request.CierreRutaEventDTO;
import com.logistica.domain.liquidacion.models.Paquete;
import com.logistica.domain.liquidacion.models.RutaLiquidacion;
import org.springframework.stereotype.Component;

@Component
public class LiquidacionRutaMapper {

    public RutaLiquidacion toModel(CierreRutaEventDTO event) {
        return RutaLiquidacion.builder()
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
