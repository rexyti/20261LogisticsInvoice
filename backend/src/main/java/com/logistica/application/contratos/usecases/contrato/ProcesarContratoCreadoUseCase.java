package com.logistica.application.contratos.usecases.contrato;

import com.logistica.application.contratos.dtos.request.ContratoCreadoEvent;
import com.logistica.application.contratos.mappers.ContratoEventMapper;
import com.logistica.domain.contratos.models.Contrato;
import com.logistica.domain.contratos.models.Transportista;
import com.logistica.domain.contratos.repositories.ContratoRepository;
import com.logistica.domain.contratos.repositories.TransportistaContratoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcesarContratoCreadoUseCase {

    private final ContratoRepository contratoRepository;
    private final TransportistaContratoRepository transportistaRepository;
    private final ContratoEventMapper eventMapper;

    @Transactional
    public void ejecutar(ContratoCreadoEvent evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento CONTRATO_CREADO no puede ser null");
        }

        String idContrato = evento.getIdContrato();
        log.info("Procesando evento CONTRATO_CREADO para contrato: {}", idContrato);

        if (contratoRepository.existePorIdContrato(idContrato)) {
            log.info("Evento duplicado ignorado para contrato: {}", idContrato);
            return;
        }

        Contrato contrato = eventMapper.toDomain(evento);

        Transportista transportista = transportistaRepository
                .buscarPorId(contrato.getTransportista().getTransportistaId())
                .orElseGet(() -> transportistaRepository.guardar(contrato.getTransportista()));

        Contrato contratoConTransportista = Contrato.builder()
                .id(contrato.getId())
                .idContrato(contrato.getIdContrato())
                .tipoContrato(contrato.getTipoContrato())
                .transportista(transportista)
                .tipoVehiculo(contrato.getTipoVehiculo())
                .esPorParada(contrato.getEsPorParada())
                .precioParadas(contrato.getPrecioParadas())
                .precio(contrato.getPrecio())
                .fechaInicio(contrato.getFechaInicio())
                .fechaFinal(contrato.getFechaFinal())
                .seguro(contrato.getSeguro())
                .build();

        contratoRepository.guardar(contratoConTransportista);
        log.info("Contrato {} persistido localmente desde evento", idContrato);
    }
}
