package com.logistica.infrastructure.messaging.mappers;

import com.logistica.application.cierreRuta.dtos.request.ConductorEventDTO;
import com.logistica.application.cierreRuta.dtos.request.ParadaEventDTO;
import com.logistica.application.cierreRuta.dtos.request.RutaCerradaEventDTO;
import com.logistica.application.cierreRuta.dtos.request.VehiculoEventDTO;
import com.logistica.domain.cierreRuta.enums.EstadoParada;
import com.logistica.infrastructure.messaging.dtos.ParadaMensajeDTO;
import com.logistica.infrastructure.messaging.dtos.RutaCerradaMensajeDTO;
import com.logistica.infrastructure.messaging.dtos.TransportistaMensajeDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RutaCerradaMensajeMapper {

    public RutaCerradaEventDTO toApplicationEvent(RutaCerradaMensajeDTO dto) {
        RutaCerradaEventDTO evento = new RutaCerradaEventDTO();
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setRutaId(dto.getRutaId());
        evento.setFechaHoraInicioTransito(dto.getFechaHoraInicioTransito());
        evento.setFechaHoraCierre(dto.getFechaHoraCierre());
        evento.setConductor(mapConductor(dto.getConductor()));
        evento.setVehiculo(mapVehiculo(dto));
        evento.setParadas(mapParadas(dto.getParadas()));
        return evento;
    }

    private ConductorEventDTO mapConductor(TransportistaMensajeDTO dto) {
        ConductorEventDTO conductor = new ConductorEventDTO();
        conductor.setConductorId(dto.getTransportistaId());
        conductor.setNombre(dto.getNombre());
        conductor.setModeloContrato(dto.getModeloContrato());
        return conductor;
    }

    private VehiculoEventDTO mapVehiculo(RutaCerradaMensajeDTO dto) {
        VehiculoEventDTO vehiculo = new VehiculoEventDTO();
        vehiculo.setVehiculoId(dto.getVehiculo().getVehiculoId());
        vehiculo.setTipo(dto.getVehiculo().getTipo());
        return vehiculo;
    }

    private List<ParadaEventDTO> mapParadas(List<ParadaMensajeDTO> paradas) {
        return paradas.stream()
                .map(this::mapParada)
                .toList();
    }

    private ParadaEventDTO mapParada(ParadaMensajeDTO p) {
        ParadaEventDTO parada = new ParadaEventDTO();

        parada.setParadaId(p.getParadaId());
        parada.setPaqueteId(p.getPaqueteId());

        parada.setEstado(mapEstado(p.getEstado()));

        parada.setMotivoNoEntrega(p.getMotivoNoEntrega());
        parada.setFechaHoraGestion(p.getFechaHoraGestion());

        return parada;
    }

    private EstadoParada mapEstado(String estado) {
        if (estado == null) {
            throw new IllegalArgumentException("Estado null en mensaje");
        }

        try {
            return EstadoParada.fromValue(estado);
        } catch (Exception e) {
            throw new IllegalArgumentException("Estado inválido recibido: " + estado);
        }
    }
}
