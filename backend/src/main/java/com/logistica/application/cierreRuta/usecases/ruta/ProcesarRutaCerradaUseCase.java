package com.logistica.application.cierreRuta.usecases.ruta;

import com.logistica.application.cierreRuta.dtos.request.RutaCerradaEventDTO;
import com.logistica.application.cierreRuta.mappers.RutaEventMapper;
import com.logistica.domain.cierreRuta.models.RutaCerrada;
import com.logistica.domain.cierreRuta.models.TransportistaRuta;
import com.logistica.domain.cierreRuta.ports.EventPublisher;
import com.logistica.domain.cierreRuta.ports.TimeProvider;
import com.logistica.domain.cierreRuta.repositories.RutaRepository;
import com.logistica.domain.cierreRuta.repositories.TransportistaRutaRepository;
import com.logistica.domain.cierreRuta.services.ClasificacionRutaService;
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
    private final TransportistaRutaRepository transportistaRepository;
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

        RutaCerrada ruta = rutaEventMapper.toDomain(evento);

        TransportistaRuta transportista = ruta.getTransportista();

        TransportistaRuta existente = transportistaRepository
                .buscarPorTransportistaId(transportista.getTransportistaId())
                .orElseGet(() -> transportistaRepository.guardar(transportista));

        ruta.asignarTransportista(existente);

        clasificacionRutaService.clasificar(ruta);

        ruta.procesar(timeProvider.now());

        rutaRepository.guardar(ruta);

        ruta.obtenerEventos().forEach(eventPublisher::publish);

        ruta.limpiarEventos();

        long duracion = System.currentTimeMillis() - inicio;
        log.info("RutaCerrada {} procesada en {} ms", rutaId, duracion);
    }

    private void validarEvento(RutaCerradaEventDTO evento) {
        if (evento == null) {
            throw new IllegalArgumentException("Evento no puede ser null");
        }
    }
}
