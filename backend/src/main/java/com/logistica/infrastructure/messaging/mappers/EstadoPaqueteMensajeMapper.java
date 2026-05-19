package com.logistica.infrastructure.messaging.mappers;

import com.logistica.application.novedadEstadoPaquete.dtos.request.NovedadEstadoPaqueteEvent;
import com.logistica.infrastructure.messaging.dtos.EstadoPaqueteMensajeDTO;
import org.springframework.stereotype.Component;

@Component
public class EstadoPaqueteMensajeMapper {

    public NovedadEstadoPaqueteEvent toApplicationEvent(EstadoPaqueteMensajeDTO dto) {
        NovedadEstadoPaqueteEvent evento = new NovedadEstadoPaqueteEvent();
        evento.setIdPaquete(dto.getIdPaquete());
        evento.setIdRuta(dto.getIdRuta());
        evento.setEstado(dto.getEstado());
        return evento;
    }
}
