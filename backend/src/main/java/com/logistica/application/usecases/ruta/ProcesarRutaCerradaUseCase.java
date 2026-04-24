package com.logistica.application.usecases.ruta;

import com.logistica.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.application.mappers.RutaEventMapper;
import com.logistica.domain.enums.EstadoProcesamiento;
import com.logistica.domain.enums.TipoAlertaRuta;
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

import java.util.Objects;
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


        Objects.requireNonNull(evento, "Evento de ruta no puede ser null");

        UUID rutaId = evento.getRutaId();
        long inicio = System.currentTimeMillis();

        log.info("Iniciando procesamiento de RUTA_CERRADA para ruta_id: {}", rutaId);


        if (rutaRepository.existsByRutaId(rutaId)) {
            log.info("Evento duplicado ignorado para ruta_id: {}", rutaId);
            return;
        }


        Ruta ruta = rutaEventMapper.toDomain(evento);


        if (ruta.getModeloContrato() == null) {
            handleError(
                    ruta,
                    rutaId,
                    EstadoProcesamiento.REQUIERE_REVISION,
                    TipoAlertaRuta.CONTRATO_NULO,
                    "El modelo de contrato no fue encontrado",
                    evento
            );
            return;
        }

        if (ruta.getTipoVehiculo() == null || ruta.getTipoVehiculo().isBlank()) {
            handleError(
                    ruta,
                    rutaId,
                    EstadoProcesamiento.REQUIERE_REVISION,
                    TipoAlertaRuta.VEHICULO_DESCONOCIDO,
                    "El tipo de vehículo es inválido: " + ruta.getTipoVehiculo(),
                    evento
            );
            return;
        }

        clasificacionRutaService.clasificar(ruta);


        ruta.setEstadoProcesamiento(EstadoProcesamiento.OK);


        int totalParadas = ruta.getParadas() == null ? 0 : ruta.getParadas().size();
        rutaValidator.validar(ruta, totalParadas);


        rutaRepository.guardar(ruta);

        long duracion = System.currentTimeMillis() - inicio;
        log.info("Ruta {} procesada correctamente en {}ms", rutaId, duracion);
    }

    private void handleError(
            Ruta ruta,
            UUID rutaId,
            EstadoProcesamiento estado,
            TipoAlertaRuta tipoAlerta,
            String detalle,
            RutaCerradaEventDTO evento
    ) {

        log.warn("Error en ruta_id {}: {}", rutaId, detalle);

        ruta.setEstadoProcesamiento(estado);

        eventPublisher.publishEvent(
                new RutaCerradaProcesadaEvent(
                        rutaId,
                        estado,
                        tipoAlerta,
                        detalle,
                        evento.getFechaHoraCierre()
                )
        );

        rutaRepository.guardar(ruta);
    }
}