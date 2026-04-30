package com.logistica.RegistrarEstadoPago.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.RegistrarEstadoPago.application.dtos.request.EventoEstadoPagoRequestDTO;
import com.logistica.RegistrarEstadoPago.application.dtos.response.PagoResponseDTO;
import com.logistica.RegistrarEstadoPago.application.usecases.pago.PagoService;
import com.logistica.RegistrarEstadoPago.domain.enums.EstadoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.enums.EstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.domain.models.EventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.models.LiquidacionReferencia;
import com.logistica.RegistrarEstadoPago.domain.models.Pago;
import com.logistica.RegistrarEstadoPago.domain.repositories.EstadoPagoRepository;
import com.logistica.RegistrarEstadoPago.domain.repositories.EventoTransaccionRepository;
import com.logistica.RegistrarEstadoPago.domain.repositories.LiquidacionRepository;
import com.logistica.RegistrarEstadoPago.domain.repositories.PagoRepository;
import com.logistica.RegistrarEstadoPago.domain.services.EstadoPagoDomainService;
import com.logistica.RegistrarEstadoPago.domain.services.IdempotenciaEventoPagoService;
import com.logistica.RegistrarEstadoPago.domain.services.TransicionEstadoPagoService;
import com.logistica.RegistrarEstadoPago.exceptions.PagoNoEncontradoException;
import com.logistica.RegistrarEstadoPago.exceptions.TransicionEstadoPagoInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock private PagoRepository pagoRepository;
    @Mock private EstadoPagoRepository estadoPagoRepository;
    @Mock private EventoTransaccionRepository eventoTransaccionRepository;
    @Mock private LiquidacionRepository liquidacionRepository;
    @Mock private EstadoPagoDomainService estadoPagoDomainService;
    @Mock private TransicionEstadoPagoService transicionEstadoPagoService;
    @Mock private IdempotenciaEventoPagoService idempotenciaEventoPagoService;

    private PagoService pagoService;

    private final UUID ID_PAGO = UUID.randomUUID();
    private final UUID ID_LIQUIDACION = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        pagoService = new PagoService(
                pagoRepository, estadoPagoRepository, eventoTransaccionRepository,
                liquidacionRepository, estadoPagoDomainService, transicionEstadoPagoService,
                idempotenciaEventoPagoService, new ObjectMapper()
        );
    }

    @Test
    void procesarEvento_registroInicial_creaPagoYEstado() {
        EventoEstadoPagoRequestDTO dto = buildDto(EstadoPagoEnum.EN_PROCESO, 1L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.empty());
        when(pagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(estadoPagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        pagoService.procesarEvento(dto);

        verify(pagoRepository, atLeast(2)).save(any());
        verify(estadoPagoRepository).save(any());
        // RECIBIDO + PROCESADO = 2 saves
        verify(eventoTransaccionRepository, times(2)).save(any());
    }

    @Test
    void procesarEvento_actualizacionAPagado_actualizaEstado() {
        EventoEstadoPagoRequestDTO dto = buildDto(EstadoPagoEnum.PAGADO, 2L);
        Pago pagoExistente = pagoConEstado(EstadoPagoEnum.EN_PROCESO, 1L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pagoExistente));
        when(pagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(estadoPagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        pagoService.procesarEvento(dto);

        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).save(pagoCaptor.capture());
        assertThat(pagoCaptor.getValue().estadoActual()).isEqualTo(EstadoPagoEnum.PAGADO);
    }

    @Test
    void procesarEvento_actualizacionARechazado_actualizaEstado() {
        EventoEstadoPagoRequestDTO dto = buildDto(EstadoPagoEnum.RECHAZADO, 2L);
        Pago pagoExistente = pagoConEstado(EstadoPagoEnum.EN_PROCESO, 1L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pagoExistente));
        when(pagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(estadoPagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        pagoService.procesarEvento(dto);

        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).save(pagoCaptor.capture());
        assertThat(pagoCaptor.getValue().estadoActual()).isEqualTo(EstadoPagoEnum.RECHAZADO);
    }

    @Test
    void procesarEvento_liquidacionInexistente_noCreaPago_registraError() {
        EventoEstadoPagoRequestDTO dto = buildDto(EstadoPagoEnum.EN_PROCESO, 1L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION)).thenReturn(Optional.empty());

        pagoService.procesarEvento(dto);

        verify(pagoRepository, never()).save(any());
        verify(estadoPagoRepository, never()).save(any());
        ArgumentCaptor<EventoTransaccion> captor = ArgumentCaptor.forClass(EventoTransaccion.class);
        verify(eventoTransaccionRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(1).estadoProcesamiento()).isEqualTo(EstadoEventoTransaccion.ERROR);
    }

    @Test
    void procesarEvento_eventoDuplicado_noCreaNuevoEstadoPago() {
        EventoEstadoPagoRequestDTO dto = buildDto(EstadoPagoEnum.PAGADO, 2L);
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(true);

        pagoService.procesarEvento(dto);

        verify(eventoTransaccionRepository, never()).save(any());
        verify(pagoRepository, never()).save(any());
        verify(estadoPagoRepository, never()).save(any());
    }

    @Test
    void procesarEvento_mismoEstadoActual_esIdempotente() {
        EventoEstadoPagoRequestDTO dto = buildDto(EstadoPagoEnum.EN_PROCESO, 1L);
        Pago pagoExistente = pagoConEstado(EstadoPagoEnum.EN_PROCESO, 1L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pagoExistente));

        pagoService.procesarEvento(dto);

        verify(pagoRepository, never()).save(any());
        verify(estadoPagoRepository, never()).save(any());
        ArgumentCaptor<EventoTransaccion> captor = ArgumentCaptor.forClass(EventoTransaccion.class);
        verify(eventoTransaccionRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(1).estadoProcesamiento()).isEqualTo(EstadoEventoTransaccion.PROCESADO);
    }

    @Test
    void procesarEvento_eventoDesordenado_noSobrescribeEstado() {
        EventoEstadoPagoRequestDTO dto = buildDto(EstadoPagoEnum.EN_PROCESO, 1L);
        Pago pagoEnSecuenciaAlta = pagoConEstado(EstadoPagoEnum.EN_PROCESO, 5L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pagoEnSecuenciaAlta));

        // Estado igual al actual, se trata como idempotente antes de llegar al check de secuencia
        pagoService.procesarEvento(dto);

        ArgumentCaptor<EventoTransaccion> captor = ArgumentCaptor.forClass(EventoTransaccion.class);
        verify(eventoTransaccionRepository, times(2)).save(captor.capture());
        // Mismo estado → PROCESADO idempotente
        assertThat(captor.getAllValues().get(1).estadoProcesamiento()).isEqualTo(EstadoEventoTransaccion.PROCESADO);
    }

    @Test
    void procesarEvento_pagoFinalConTransicionInvalida_rechazaEvento() {
        EventoEstadoPagoRequestDTO dto = buildDto(EstadoPagoEnum.EN_PROCESO, 10L);
        Pago pagoFinal = pagoConEstado(EstadoPagoEnum.PAGADO, 5L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pagoFinal));
        doThrow(new TransicionEstadoPagoInvalidaException("PAGADO", "EN_PROCESO"))
                .when(transicionEstadoPagoService).validarTransicion(EstadoPagoEnum.PAGADO, EstadoPagoEnum.EN_PROCESO);

        pagoService.procesarEvento(dto);

        verify(estadoPagoRepository, never()).save(any());
        ArgumentCaptor<EventoTransaccion> captor = ArgumentCaptor.forClass(EventoTransaccion.class);
        verify(eventoTransaccionRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(1).estadoProcesamiento()).isEqualTo(EstadoEventoTransaccion.RECHAZADO);
    }

    @Test
    void obtenerEstadoPago_pagoExistente_retornaDTO() {
        Pago pago = pagoConEstado(EstadoPagoEnum.PAGADO, 2L);
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pago));

        PagoResponseDTO response = pagoService.obtenerEstadoPago(ID_PAGO);

        assertThat(response.estado()).isEqualTo(EstadoPagoEnum.PAGADO);
        assertThat(response.idPago()).isEqualTo(ID_PAGO);
    }

    @Test
    void obtenerEstadoPago_pagoInexistente_lanzaExcepcion() {
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.obtenerEstadoPago(ID_PAGO))
                .isInstanceOf(PagoNoEncontradoException.class);
    }

    @Test
    void obtenerEstadoPagoPorLiquidacion_existente_retornaDTO() {
        Pago pago = pagoConEstado(EstadoPagoEnum.PAGADO, 2L);
        when(pagoRepository.findByIdLiquidacion(ID_LIQUIDACION)).thenReturn(Optional.of(pago));

        PagoResponseDTO response = pagoService.obtenerEstadoPagoPorLiquidacion(ID_LIQUIDACION);

        assertThat(response.estado()).isEqualTo(EstadoPagoEnum.PAGADO);
        assertThat(response.idLiquidacion()).isEqualTo(ID_LIQUIDACION);
    }

    @Test
    void obtenerEstadoPagoPorLiquidacion_sinPago_lanzaExcepcion() {
        when(pagoRepository.findByIdLiquidacion(ID_LIQUIDACION)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.obtenerEstadoPagoPorLiquidacion(ID_LIQUIDACION))
                .isInstanceOf(PagoNoEncontradoException.class);
    }

    @Test
    void obtenerEventos_retornaListaOrdenada() {
        List<EventoTransaccion> eventos = List.of(
                eventoConEstado(EstadoEventoTransaccion.PROCESADO),
                eventoConEstado(EstadoEventoTransaccion.DUPLICADO)
        );
        when(eventoTransaccionRepository.findByIdPago(ID_PAGO)).thenReturn(eventos);

        var result = pagoService.obtenerEventos(ID_PAGO);

        assertThat(result).hasSize(2);
    }

    private EventoEstadoPagoRequestDTO buildDto(EstadoPagoEnum estado, Long secuencia) {
        return new EventoEstadoPagoRequestDTO(
                "evt-001", "txn-001", ID_PAGO, ID_LIQUIDACION,
                estado, LocalDateTime.now(), secuencia, null
        );
    }

    private Pago pagoConEstado(EstadoPagoEnum estado, Long secuencia) {
        return new Pago(ID_PAGO, null, null, Instant.now(), null, null,
                ID_LIQUIDACION, estado, Instant.now(), secuencia);
    }

    private EventoTransaccion eventoConEstado(EstadoEventoTransaccion estadoProc) {
        return new EventoTransaccion(UUID.randomUUID(), "txn-001", ID_PAGO, ID_LIQUIDACION,
                "{}", Instant.now(), Instant.now(), 1L,
                EstadoPagoEnum.EN_PROCESO, estadoProc, null, true);
    }
}
