package com.logistica.application.usecases.ruta;

import com.logistica.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.application.mappers.RutaEventMapper;
import com.logistica.domain.enums.EstadoProcesamiento;
import com.logistica.domain.enums.TipoAlertaRuta;
import com.logistica.domain.enums.TipoVehiculo;
import com.logistica.domain.events.RutaCerradaProcesadaEvent;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.RutaRepository;
import com.logistica.domain.repositories.TarifaRepository;
import com.logistica.domain.services.ClasificacionRutaService;
import com.logistica.domain.validators.RutaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcesarRutaCerradaUseCase {

    private final RutaRepository rutaRepository;
    private final TarifaRepository tarifaRepository;
    private final RutaEventMapper rutaEventMapper;
    private final RutaValidator rutaValidator;
    private final ClasificacionRutaService clasificacionRutaService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void ejecutar(RutaCerradaEventDTO evento) {

        if (evento == null) {
            throw new IllegalArgumentException("Evento no puede ser null");
        }

        UUID rutaId = evento.getRutaId();
        long inicio = System.currentTimeMillis();

        log.info("Iniciando procesamiento de RUTA_CERRADA para ruta_id: {}", rutaId);


        if (rutaRepository.existsByRutaId(rutaId)) {
            log.info("Evento duplicado ignorado para ruta_id: {}", rutaId);
            return;
        }


        Ruta ruta = rutaEventMapper.toDomain(evento);


        EstadoProcesamiento estado = EstadoProcesamiento.OK;


        if (ruta.getModeloContrato() == null) {
            log.warn("Contrato nulo para ruta_id: {}", rutaId);
            eventPublisher.publishEvent(
                    new RutaCerradaProcesadaEvent(
                            rutaId,
                            EstadoProcesamiento.REQUIERE_REVISION,
                            TipoAlertaRuta.CONTRATO_NULO,
                            "El modelo de contrato no fue encontrado",
                            LocalDateTime.now()
                    )
            );
            estado = EstadoProcesamiento.REQUIERE_REVISION;
        }


        if (ruta.getTipoVehiculo() == null || !TipoVehiculo.isKnown(ruta.getTipoVehiculo())) {
            log.warn("Tipo de vehículo no reconocido o nulo en ruta_id: {}", rutaId);

            eventPublisher.publishEvent(
                    new RutaCerradaProcesadaEvent(
                            rutaId,
                            EstadoProcesamiento.REQUIERE_REVISION,
                            TipoAlertaRuta.VEHICULO_DESCONOCIDO,
                            "El tipo de vehículo es desconocido o nulo: " + ruta.getTipoVehiculo(),
                            LocalDateTime.now()
                    )
            );

            estado = EstadoProcesamiento.REQUIERE_REVISION;
        }


        clasificacionRutaService.clasificar(ruta);


        ruta.setEstadoProcesamiento(estado);


        rutaValidator.validar(ruta, ruta.getParadas().size());


        rutaRepository.guardar(ruta);

        long duracion = System.currentTimeMillis() - inicio;
        log.info("Ruta {} procesada con estado {} en {}ms", rutaId, estado, duracion);
    }
}