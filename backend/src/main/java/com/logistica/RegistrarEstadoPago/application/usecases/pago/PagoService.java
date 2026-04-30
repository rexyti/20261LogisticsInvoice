package com.logistica.RegistrarEstadoPago.application.usecases.pago;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.RegistrarEstadoPago.application.dtos.request.EventoEstadoPagoRequestDTO;
import com.logistica.RegistrarEstadoPago.application.dtos.response.EventoTransaccionResponseDTO;
import com.logistica.RegistrarEstadoPago.application.dtos.response.PagoResponseDTO;
import com.logistica.RegistrarEstadoPago.application.dtos.response.RecepcionEventoPagoResponseDTO;
import com.logistica.RegistrarEstadoPago.application.ports.EventoPagoAsyncPort;
import com.logistica.RegistrarEstadoPago.domain.enums.EstadoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.enums.EstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.domain.models.EstadoPago;
import com.logistica.RegistrarEstadoPago.domain.models.EventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.models.Pago;
import com.logistica.RegistrarEstadoPago.domain.repositories.EstadoPagoRepository;
import com.logistica.RegistrarEstadoPago.domain.repositories.EventoTransaccionRepository;
import com.logistica.RegistrarEstadoPago.domain.repositories.LiquidacionRepository;
import com.logistica.RegistrarEstadoPago.domain.repositories.PagoRepository;
import com.logistica.RegistrarEstadoPago.domain.services.EstadoPagoDomainService;
import com.logistica.RegistrarEstadoPago.domain.services.IdempotenciaEventoPagoService;
import com.logistica.RegistrarEstadoPago.domain.services.TransicionEstadoPagoService;
import com.logistica.RegistrarEstadoPago.exceptions.PagoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService implements RecibirEventoPagoUseCase, ProcesarEventoPagoUseCase,
        RegistrarEstadoPagoUseCase, ActualizarEstadoPagoUseCase,
        ObtenerEstadoPagoUseCase, ObtenerEventosTransaccionUseCase {

    private final PagoRepository pagoRepository;
    private final EstadoPagoRepository estadoPagoRepository;
    private final EventoTransaccionRepository eventoTransaccionRepository;
    private final LiquidacionRepository liquidacionRepository;
    private final EstadoPagoDomainService estadoPagoDomainService;
    private final TransicionEstadoPagoService transicionEstadoPagoService;
    private final IdempotenciaEventoPagoService idempotenciaEventoPagoService;
    private final ObjectMapper objectMapper;

    @Autowired
    @Lazy
    private EventoPagoAsyncPort eventoPagoAsyncPort;

    @Override
    public RecepcionEventoPagoResponseDTO recibirEvento(EventoEstadoPagoRequestDTO dto) {
        eventoPagoAsyncPort.procesarAsync(dto);
        return new RecepcionEventoPagoResponseDTO(
                "Evento de pago recibido correctamente",
                dto.idEvento(),
                dto.idTransaccionBanco(),
                "ASINCRONO"
        );
    }

    @Override
    @Transactional
    public void procesarEvento(EventoEstadoPagoRequestDTO dto) {
        Instant ahora = Instant.now();
        Instant fechaEvento = dto.fechaEvento() != null
                ? dto.fechaEvento().toInstant(ZoneOffset.UTC)
                : ahora;
        long secuencia = dto.secuencia() != null ? dto.secuencia() : 0L;
        UUID idEvento = UUID.nameUUIDFromBytes(dto.idEvento().getBytes());
        String payload = serializarPayload(dto);

        // 1. Verificar duplicado antes de guardar para no sobreescribir evento procesado
        if (idempotenciaEventoPagoService.esEventoDuplicado(dto.idTransaccionBanco())) {
            log.info("Evento duplicado ignorado: {}", dto.idTransaccionBanco());
            return;
        }

        // 2. Guardar EventoTransaccion con estado RECIBIDO
        EventoTransaccion evento = new EventoTransaccion(
                idEvento, dto.idTransaccionBanco(), dto.idPago(), dto.idLiquidacion(),
                payload, ahora, fechaEvento, secuencia,
                dto.estado(), EstadoEventoTransaccion.RECIBIDO, null, false
        );
        eventoTransaccionRepository.save(evento);

        // 3. Validar que la liquidación exista
        if (liquidacionRepository.findById(dto.idLiquidacion()).isEmpty()) {
            log.warn("Liquidación no encontrada: {}", dto.idLiquidacion());
            eventoTransaccionRepository.save(evento.conEstadoProcesamiento(
                    EstadoEventoTransaccion.ERROR, "Liquidación no encontrada: " + dto.idLiquidacion()));
            return;
        }

        // 4. Buscar o crear el Pago
        Pago pago = pagoRepository.findById(dto.idPago()).orElse(null);

        if (pago == null) {
            registrarEstadoInicial(dto.idPago(), dto.idLiquidacion(), dto.estado(), fechaEvento, secuencia, idEvento);
            eventoTransaccionRepository.save(evento.conEstadoProcesamiento(EstadoEventoTransaccion.PROCESADO, null));
            return;
        }

        // 5. Idempotencia por mismo estado
        if (pago.estadoActual() == dto.estado()) {
            log.info("Evento con mismo estado actual, idempotente: idPago={}, estado={}", dto.idPago(), dto.estado());
            eventoTransaccionRepository.save(evento.conEstadoProcesamiento(EstadoEventoTransaccion.PROCESADO, null));
            return;
        }

        // 6. Verificar que el evento no sea desordenado
        if (secuencia > 0 && pago.ultimaSecuenciaProcesada() != null && secuencia < pago.ultimaSecuenciaProcesada()) {
            log.warn("Evento desordenado: secuencia={} < ultimaSecuencia={}", secuencia, pago.ultimaSecuenciaProcesada());
            eventoTransaccionRepository.save(evento.conEstadoProcesamiento(EstadoEventoTransaccion.RECHAZADO,
                    "Evento desordenado: no se permite sobrescribir un estado final o más reciente"));
            return;
        }

        // 7. Validar transición de estado
        try {
            transicionEstadoPagoService.validarTransicion(pago.estadoActual(), dto.estado());
        } catch (Exception ex) {
            log.warn("Transición de estado inválida: {} -> {}", pago.estadoActual(), dto.estado());
            eventoTransaccionRepository.save(evento.conEstadoProcesamiento(
                    EstadoEventoTransaccion.RECHAZADO, ex.getMessage()));
            return;
        }

        // 8. Actualizar estado del pago
        actualizarEstadoPago(dto.idPago(), dto.estado(), fechaEvento, secuencia, idEvento);
        eventoTransaccionRepository.save(evento.conEstadoProcesamiento(EstadoEventoTransaccion.PROCESADO, null));
    }

    @Override
    public void registrarEstadoInicial(UUID idPago, UUID idLiquidacion, EstadoPagoEnum estado,
                                        Instant fechaEvento, Long secuencia, UUID idEvento) {
        estadoPagoDomainService.validarEstadoConocido(estado);

        Pago nuevoPago = new Pago(idPago, null, null, Instant.now(), null, null,
                idLiquidacion, EstadoPagoEnum.PENDIENTE, Instant.now(), 0L);
        pagoRepository.save(nuevoPago);

        transicionEstadoPagoService.validarTransicion(EstadoPagoEnum.PENDIENTE, estado);

        EstadoPago estadoPago = new EstadoPago(null, idPago, estado, Instant.now(), fechaEvento, secuencia, idEvento);
        estadoPagoRepository.save(estadoPago);

        Pago pagoActualizado = nuevoPago.actualizarEstado(estado, Instant.now(), secuencia);
        pagoRepository.save(pagoActualizado);
    }

    @Override
    public void actualizarEstadoPago(UUID idPago, EstadoPagoEnum nuevoEstado,
                                      Instant fechaEvento, Long secuencia, UUID idEvento) {
        estadoPagoDomainService.validarEstadoConocido(nuevoEstado);

        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PagoNoEncontradoException(idPago.toString()));

        EstadoPago estadoPago = new EstadoPago(null, idPago, nuevoEstado, Instant.now(), fechaEvento, secuencia, idEvento);
        estadoPagoRepository.save(estadoPago);

        Pago pagoActualizado = pago.actualizarEstado(nuevoEstado, Instant.now(), secuencia);
        pagoRepository.save(pagoActualizado);
    }

    @Override
    public PagoResponseDTO obtenerEstadoPago(UUID idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new PagoNoEncontradoException(idPago.toString()));
        return new PagoResponseDTO(pago.idPago(), pago.idLiquidacion(), pago.estadoActual(),
                pago.fechaUltimaActualizacion(), pago.ultimaSecuenciaProcesada());
    }

    @Override
    public PagoResponseDTO obtenerEstadoPagoPorLiquidacion(UUID idLiquidacion) {
        Pago pago = pagoRepository.findByIdLiquidacion(idLiquidacion)
                .orElseThrow(() -> new PagoNoEncontradoException("liquidacion:" + idLiquidacion));
        return new PagoResponseDTO(pago.idPago(), pago.idLiquidacion(), pago.estadoActual(),
                pago.fechaUltimaActualizacion(), pago.ultimaSecuenciaProcesada());
    }

    @Override
    public List<EventoTransaccionResponseDTO> obtenerEventos(UUID idPago) {
        return eventoTransaccionRepository.findByIdPago(idPago).stream()
                .map(e -> new EventoTransaccionResponseDTO(
                        e.idEvento(), e.idTransaccionBanco(), e.idPago(), e.idLiquidacion(),
                        e.estadoSolicitado(), e.estadoProcesamiento(),
                        e.fechaRecepcion(), e.fechaEventoBanco(), e.secuencia(), e.mensajeError()))
                .toList();
    }

    private String serializarPayload(EventoEstadoPagoRequestDTO dto) {
        if (dto.payloadOriginal() == null) return null;
        try {
            return objectMapper.writeValueAsString(dto.payloadOriginal());
        } catch (JsonProcessingException e) {
            return dto.payloadOriginal().toString();
        }
    }
}
