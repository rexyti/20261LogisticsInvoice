package com.logistica.liquidacion.infrastructure.persistence.mapper;

import com.logistica.liquidacion.application.dtos.request.CierreRutaEventDTO;
import com.logistica.liquidacion.domain.models.Paquete;
import com.logistica.liquidacion.domain.models.Ruta;
import org.springframework.stereotype.Component;

@Component
public class RutaMapper {

    public Ruta toModel(CierreRutaEventDTO event) {
        return Ruta.builder()
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