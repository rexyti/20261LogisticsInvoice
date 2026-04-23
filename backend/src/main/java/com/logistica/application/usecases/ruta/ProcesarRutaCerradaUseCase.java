package com.logistica.application.usecases.ruta;

import com.logistica.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.application.mappers.RutaEventMapper;
import com.logistica.domain.enums.EstadoProcesamiento;
import com.logistica.domain.enums.TipoAlertaRuta;
import com.logistica.domain.enums.TipoVehiculo;
import com.logistica.domain.events.RutaCerradaProcesadaEvent;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.RutaRepository;
import com.logistica.domain.services.ClasificacionRutaService;
import com.logistica.domain.validators.RutaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcesarRutaCerradaUseCase {

    private final RutaRepository rutaRepository;
    private final RutaEventMapper rutaEventMapper;
    private final RutaValidator rutaValidator;
    private final ClasificacionRutaService clasificacionRutaService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void ejecutar(RutaCerradaEventDTO evento) {

        UUID rutaId = evento.getRutaId();
        long inicio = System.currentTimeMillis();

        log.info("Iniciando procesamiento de RUTA_CERRADA for ruta_id: {}", rutaId);

        // 1. Idempotencia
        if (rutaRepository.existsByRutaId(rutaId)) {
            log.info("Evento duplicado ignorado for ruta_id: {}", rutaId);
            return;
        }


        Ruta ruta = rutaEventMapper.toDomain(evento);


        EstadoProcesamiento estado = EstadoProcesamiento.OK;


        if (ruta.getModeloContrato() == null) {
            log.warn("Contrato nulo for ruta_id: {}", rutaId);
            eventPublisher.publishEvent(
                    new RutaCerradaProcesadaEvent(rutaId, EstadoProcesamiento.REQUIERE_REVISION, TipoAlertaRuta.CONTRATO_NULO, null, evento.getFechaHoraCierre())
            );
            estado = EstadoProcesamiento.REQUIERE_REVISION;
        }


        if (ruta.getTipoVehiculo() == null || ruta.getTipoVehiculo().isBlank()) {
            log.warn("Tipo de vehículo no encontrado en ruta_id: {}", rutaId);

            eventPublisher.publishEvent(
                    new RutaCerradaProcesadaEvent(rutaId, EstadoProcesamiento.REQUIERE_REVISION, TipoAlertaRuta.VEHICULO_DESCONOCIDO, ruta.getTipoVehiculo(), evento.getFechaHoraCierre())
            );

            estado = EstadoProcesamiento.REQUIERE_REVISION;
        }

        // 5. Lógica de negocio (delegada)
        clasificacionRutaService.clasificar(ruta);

        // 6. Set estado final
        ruta.setEstadoProcesamiento(estado);

        // 7. Validación final
        rutaValidator.validar(ruta, ruta.getParadas().size());

        // 8. Persistencia
        rutaRepository.guardar(ruta);

        long duracion = System.currentTimeMillis() - inicio;
        log.info("Ruta {} procesada con estado {} en {}ms", rutaId, estado, duracion);
    }
}
