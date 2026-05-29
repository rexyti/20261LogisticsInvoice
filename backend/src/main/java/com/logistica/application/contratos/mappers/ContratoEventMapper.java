package com.logistica.application.contratos.mappers;

import com.logistica.application.contratos.dtos.request.ContratoCreadoEvent;
import com.logistica.domain.contratos.models.Contrato;
import com.logistica.domain.contratos.models.Seguro;
import com.logistica.domain.contratos.models.Transportista;
import com.logistica.domain.shared.enums.TipoVehiculo;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ContratoEventMapper {

    public Contrato toDomain(ContratoCreadoEvent evento) {
        Seguro seguro = null;
        if (evento.getSeguro() != null) {
            seguro = Seguro.builder()
                    .idSeguro(UUID.randomUUID())
                    .numeroPoliza(evento.getSeguro().getNumeroPoliza())
                    .estado(evento.getSeguro().getEstado())
                    .build();
        }

        Transportista transportista = Transportista.builder()
                .transportistaId(evento.getTransportista().getTransportistaId())
                .nombre(evento.getTransportista().getNombre())
                .build();

        return Contrato.builder()
                .id(UUID.randomUUID())
                .idContrato(evento.getIdContrato())
                .tipoContrato(evento.getTipoContrato())
                .transportista(transportista)
                .tipoVehiculo(TipoVehiculo.valueOf(evento.getTipoVehiculo()))
                .esPorParada(evento.getEsPorParada())
                .precioParadas(evento.getPrecioParadas())
                .precio(evento.getPrecio())
                .fechaInicio(evento.getFechaInicio())
                .fechaFinal(evento.getFechaFinal())
                .seguro(seguro)
                .build();
    }
}
