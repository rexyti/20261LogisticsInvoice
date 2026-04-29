package com.logistica.cierreRuta.application.usecases.ruta;

import com.logistica.cierreRuta.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.cierreRuta.application.mappers.RutaEventMapper;
import com.logistica.cierreRuta.domain.models.Ruta;
import com.logistica.cierreRuta.domain.models.Transportista;
import com.logistica.cierreRuta.domain.ports.EventPublisher;
import com.logistica.cierreRuta.domain.ports.TimeProvider;
import com.logistica.cierreRuta.domain.repositories.RutaRepository;

import com.logistica.cierreRuta.domain.repositories.TransportistaRepository;
import com.logistica.cierreRuta.domain.services.ClasificacionRutaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcesarRutaCerradaUseCase {

    private final RutaRepository rutaRepository;
    private final TransportistaRepository transportistaRepository;
    private final RutaEventMapper rutaEventMapper;
    private final TimeProvider timeProvider;
    private final ClasificacionRutaService clasificacionRutaService;
    private final EventPublisher eventPublisher;

    @Transactional
    public void ejecutar(RutaCerradaEventDTO evento) {

        validarEvento(evento);

        UUID rutaId = evento.getRutaId();
        long inicio = System.currentTimeMillis();

        log.info("Iniciando proceso de RUTA_CERRADA para ruta_id: {}", rutaId);

        if (rutaRepository.existsByRutaId(rutaId)) {
            log.info("Evento duplicado ignorado para ruta_id {}", rutaId);
            return;
        }

        Ruta ruta = rutaEventMapper.toDomain(evento);


        Transportista transportista = ruta.getTransportista();


        Transportista existente = transportistaRepository
                .buscarPorTransportistaId(transportista.getTransportistaId())
                .orElseGet(() -> transportistaRepository.guardar(transportista));

        ruta.asignarTransportista(existente);

        // ----------------------

        clasificacionRutaService.clasificar(ruta);

        ruta.procesar(timeProvider.now());

        rutaRepository.guardar(ruta);

        ruta.obtenerEventos().forEach(eventPublisher::publish);

        ruta.limpiarEventos();

        long duracion = System.currentTimeMillis() - inicio;
        log.info("Ruta {} procesada en {} ms", rutaId, duracion);
    }

    private void validarEvento(RutaCerradaEventDTO evento) {
        if (evento == null) {
            throw new IllegalArgumentException("Evento no puede ser null");
        }
    }
}