package com.logistica.infrastructure.messaging.mappers;

import com.logistica.application.contratos.dtos.request.ContratoCreadoEvent;
import com.logistica.application.contratos.dtos.request.SeguroEventDTO;
import com.logistica.application.contratos.dtos.request.TransportistaEventDTO;
import com.logistica.infrastructure.messaging.dtos.ContratoCreadoMensajeDTO;
import com.logistica.infrastructure.messaging.dtos.SeguroMensajeDTO;
import com.logistica.infrastructure.messaging.dtos.TransportistaMensajeDTO;
import org.springframework.stereotype.Component;

@Component
public class ContratoCreadoMensajeMapper {

    public ContratoCreadoEvent toApplicationEvent(ContratoCreadoMensajeDTO dto) {
        ContratoCreadoEvent evento = new ContratoCreadoEvent();
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setIdContrato(dto.getIdContrato());
        evento.setTipoContrato(dto.getTipoContrato());
        evento.setTransportista(mapTransportista(dto.getTransportista()));
        evento.setTipoVehiculo(dto.getTipoVehiculo());
        evento.setEsPorParada(dto.getEsPorParada());
        evento.setPrecioParadas(dto.getPrecioParadas());
        evento.setPrecio(dto.getPrecio());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFinal(dto.getFechaFinal());
        evento.setSeguro(dto.getSeguro() != null ? mapSeguro(dto.getSeguro()) : null);
        return evento;
    }

    private TransportistaEventDTO mapTransportista(TransportistaMensajeDTO dto) {
        TransportistaEventDTO transportista = new TransportistaEventDTO();
        transportista.setTransportistaId(dto.getTransportistaId());
        transportista.setNombre(dto.getNombre());
        return transportista;
    }

    private SeguroEventDTO mapSeguro(SeguroMensajeDTO dto) {
        SeguroEventDTO seguro = new SeguroEventDTO();
        seguro.setNumeroPoliza(dto.getNumeroPoliza());
        seguro.setEstado(dto.getEstado());
        return seguro;
    }
}
