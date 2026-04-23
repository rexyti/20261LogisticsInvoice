package com.logistica.application.usecases.ruta;

import com.logistica.application.dtos.request.ParadaEventDTO;
import com.logistica.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.EstadoProcesamiento;
import com.logistica.domain.enums.MotivoFalla;
import com.logistica.domain.enums.TipoVehiculo;
import com.logistica.domain.events.RutaCerradaProcesadaEvent;
import com.logistica.domain.models.Parada;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.models.Transportista;
import com.logistica.domain.repositories.RutaRepository;
import com.logistica.domain.validators.RutaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcesarRutaCerradaUseCase {

    private final RutaRepository rutaRepository;
    private final RutaValidator rutaValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void ejecutar(RutaCerradaEventDTO evento) {
        UUID rutaId = evento.getRutaId();
        long inicio = System.currentTimeMillis();
        log.info("Iniciando procesamiento de RUTA_CERRADA para ruta_id: {}", rutaId);

        if (rutaRepository.existsByRutaId(rutaId)) {
            log.info("Evento duplicado ignorado para ruta_id: {}", rutaId);
            return;
        }

        EstadoProcesamiento estado = EstadoProcesamiento.OK;

        if (evento.getConductor().getModeloContrato() == null) {
            log.warn("Contrato nulo para ruta_id: {}", rutaId);
            eventPublisher.publishEvent(new RutaCerradaProcesadaEvent(rutaId, "CONTRATO_NULO", null));
            estado = EstadoProcesamiento.REQUIERE_REVISION;
        }

        if (!TipoVehiculo.isKnown(evento.getVehiculo().getTipo())) {
            log.warn("Tarifa no encontrada para vehículo tipo {} en ruta_id: {}",
                    evento.getVehiculo().getTipo(), rutaId);
            eventPublisher.publishEvent(
                    new RutaCerradaProcesadaEvent(rutaId, "VEHICULO_DESCONOCIDO", evento.getVehiculo().getTipo()));
            estado = EstadoProcesamiento.REQUIERE_REVISION;
        }

        List<Parada> paradas = construirParadas(evento.getParadas());

        Transportista transportista = Transportista.builder()
                .conductorId(evento.getConductor().getConductorId())
                .nombre(evento.getConductor().getNombre())
                .build();

        Ruta ruta = Ruta.builder()
                .rutaId(rutaId)
                .transportista(transportista)
                .tipoVehiculo(evento.getVehiculo().getTipo())
                .modeloContrato(evento.getConductor().getModeloContrato())
                .fechaInicioTransito(evento.getFechaHoraInicioTransito())
                .fechaCierre(evento.getFechaHoraCierre())
                .estadoProcesamiento(estado)
                .paradas(paradas)
                .build();

        rutaValidator.validar(ruta, evento.getParadas().size());
        rutaRepository.guardar(ruta);

        long duracion = System.currentTimeMillis() - inicio;
        log.info("Ruta {} procesada con estado {} en {}ms", rutaId, estado, duracion);
    }

    private List<Parada> construirParadas(List<ParadaEventDTO> paradaDTOs) {
        return paradaDTOs.stream().map(dto -> {
            MotivoFalla motivo = dto.getMotivoNoEntrega() != null
                    ? MotivoFalla.fromValue(dto.getMotivoNoEntrega())
                    : null;
            return Parada.builder()
                    .paradaId(dto.getParadaId())
                    .estado(EstadoParada.valueOf(dto.getEstado()))
                    .motivoFalla(motivo)
                    .responsable(motivo != null ? motivo.getResponsable() : null)
                    .build();
        }).toList();
    }
}
