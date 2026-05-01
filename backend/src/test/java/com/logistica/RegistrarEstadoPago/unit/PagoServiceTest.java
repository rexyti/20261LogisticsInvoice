package com.logistica.RegistrarEstadoPago.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.RegistrarEstadoPago.application.dtos.request.EventoEstadoPagoRequestDTO;
import com.logistica.RegistrarEstadoPago.application.dtos.response.PagoResponseDTO;
import com.logistica.RegistrarEstadoPago.application.usecases.pago.PagoService;
import com.logistica.RegistrarEstadoPago.domain.enums.EstadoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.models.LiquidacionReferencia;
import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoPago;
import com.logistica.RegistrarEstadoPago.domain.repositories.RegistrarEstadoPagoEstadoPagoRepository;
import com.logistica.RegistrarEstadoPago.domain.repositories.EventoTransaccionRepository;
import com.logistica.RegistrarEstadoPago.domain.repositories.RegistrarEstadoPagoLiquidacionRepository;
import com.logistica.RegistrarEstadoPago.domain.repositories.RegistrarEstadoPagoPagoRepository;
import com.logistica.RegistrarEstadoPago.domain.services.EstadoPagoDomainService;
import com.logistica.RegistrarEstadoPago.domain.services.IdempotenciaEventoPagoService;
import com.logistica.RegistrarEstadoPago.domain.services.TransicionEstadoPagoService;
import com.logistica.RegistrarEstadoPago.exceptions.RegistrarEstadoPagoPagoNoEncontradoException;
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

    @Mock private RegistrarEstadoPagoPagoRepository pagoRepository;
    @Mock private RegistrarEstadoPagoEstadoPagoRepository estadoPagoRepository;
    @Mock private EventoTransaccionRepository eventoTransaccionRepository;
    @Mock private RegistrarEstadoPagoLiquidacionRepository liquidacionRepository;
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
        EventoEstadoPagoRequestDTO dto = buildDto(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, 1L);
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
        EventoEstadoPagoRequestDTO dto = buildDto(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, 2L);
        RegistrarEstadoPagoPago pagoExistente = pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, 1L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pagoExistente));
        when(pagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(estadoPagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        pagoService.procesarEvento(dto);

        ArgumentCaptor<RegistrarEstadoPagoPago> pagoCaptor = ArgumentCaptor.forClass(RegistrarEstadoPagoPago.class);
        verify(pagoRepository).save(pagoCaptor.capture());
        assertThat(pagoCaptor.getValue().estadoActual()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
    }

    @Test
    void procesarEvento_actualizacionARechazado_actualizaEstado() {
        EventoEstadoPagoRequestDTO dto = buildDto(RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO, 2L);
        RegistrarEstadoPagoPago pagoExistente = pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, 1L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pagoExistente));
        when(pagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(estadoPagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        pagoService.procesarEvento(dto);

        ArgumentCaptor<RegistrarEstadoPagoPago> pagoCaptor = ArgumentCaptor.forClass(RegistrarEstadoPagoPago.class);
        verify(pagoRepository).save(pagoCaptor.capture());
        assertThat(pagoCaptor.getValue().estadoActual()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.RECHAZADO);
    }

    @Test
    void procesarEvento_liquidacionInexistente_noCreaPago_registraError() {
        EventoEstadoPagoRequestDTO dto = buildDto(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, 1L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION)).thenReturn(Optional.empty());

        pagoService.procesarEvento(dto);

        verify(pagoRepository, never()).save(any());
        verify(estadoPagoRepository, never()).save(any());
        ArgumentCaptor<RegistrarEstadoPagoEventoTransaccion> captor = ArgumentCaptor.forClass(RegistrarEstadoPagoEventoTransaccion.class);
        verify(eventoTransaccionRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(1).estadoProcesamiento()).isEqualTo(EstadoEventoTransaccion.ERROR);
    }

    @Test
    void procesarEvento_eventoDuplicado_noCreaNuevoEstadoPago() {
        EventoEstadoPagoRequestDTO dto = buildDto(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, 2L);
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(true);

        pagoService.procesarEvento(dto);

        verify(eventoTransaccionRepository, never()).save(any());
        verify(pagoRepository, never()).save(any());
        verify(estadoPagoRepository, never()).save(any());
    }

    @Test
    void procesarEvento_mismoEstadoActual_esIdempotente() {
        EventoEstadoPagoRequestDTO dto = buildDto(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, 1L);
        RegistrarEstadoPagoPago pagoExistente = pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, 1L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pagoExistente));

        pagoService.procesarEvento(dto);

        verify(pagoRepository, never()).save(any());
        verify(estadoPagoRepository, never()).save(any());
        ArgumentCaptor<RegistrarEstadoPagoEventoTransaccion> captor = ArgumentCaptor.forClass(RegistrarEstadoPagoEventoTransaccion.class);
        verify(eventoTransaccionRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(1).estadoProcesamiento()).isEqualTo(EstadoEventoTransaccion.PROCESADO);
    }

    @Test
    void procesarEvento_eventoDesordenado_noSobrescribeEstado() {
        EventoEstadoPagoRequestDTO dto = buildDto(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, 1L);
        RegistrarEstadoPagoPago pagoEnSecuenciaAlta = pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, 5L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pagoEnSecuenciaAlta));

        // Estado igual al actual, se trata como idempotente antes de llegar al check de secuencia
        pagoService.procesarEvento(dto);

        ArgumentCaptor<RegistrarEstadoPagoEventoTransaccion> captor = ArgumentCaptor.forClass(RegistrarEstadoPagoEventoTransaccion.class);
        verify(eventoTransaccionRepository, times(2)).save(captor.capture());
        // Mismo estado → PROCESADO idempotente
        assertThat(captor.getAllValues().get(1).estadoProcesamiento()).isEqualTo(EstadoEventoTransaccion.PROCESADO);
    }

    @Test
    void procesarEvento_pagoFinalConTransicionInvalida_rechazaEvento() {
        EventoEstadoPagoRequestDTO dto = buildDto(RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, 10L);
        RegistrarEstadoPagoPago pagoFinal = pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, 5L);
        when(eventoTransaccionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(idempotenciaEventoPagoService.esEventoDuplicado(any())).thenReturn(false);
        when(liquidacionRepository.findById(ID_LIQUIDACION))
                .thenReturn(Optional.of(new LiquidacionReferencia(ID_LIQUIDACION)));
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pagoFinal));
        doThrow(new TransicionEstadoPagoInvalidaException("PAGADO", "EN_PROCESO"))
                .when(transicionEstadoPagoService).validarTransicion(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO);

        pagoService.procesarEvento(dto);

        verify(estadoPagoRepository, never()).save(any());
        ArgumentCaptor<RegistrarEstadoPagoEventoTransaccion> captor = ArgumentCaptor.forClass(RegistrarEstadoPagoEventoTransaccion.class);
        verify(eventoTransaccionRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(1).estadoProcesamiento()).isEqualTo(EstadoEventoTransaccion.RECHAZADO);
    }

    @Test
    void obtenerEstadoPago_pagoExistente_retornaDTO() {
        RegistrarEstadoPagoPago pago = pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, 2L);
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.of(pago));

        PagoResponseDTO response = pagoService.obtenerEstadoPago(ID_PAGO);

        assertThat(response.estado()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
        assertThat(response.idPago()).isEqualTo(ID_PAGO);
    }

    @Test
    void obtenerEstadoPago_pagoInexistente_lanzaExcepcion() {
        when(pagoRepository.findById(ID_PAGO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.obtenerEstadoPago(ID_PAGO))
                .isInstanceOf(RegistrarEstadoPagoPagoNoEncontradoException.class);
    }

    @Test
    void obtenerEstadoPagoPorLiquidacion_existente_retornaDTO() {
        RegistrarEstadoPagoPago pago = pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum.PAGADO, 2L);
        when(pagoRepository.findByIdLiquidacion(ID_LIQUIDACION)).thenReturn(Optional.of(pago));

        PagoResponseDTO response = pagoService.obtenerEstadoPagoPorLiquidacion(ID_LIQUIDACION);

        assertThat(response.estado()).isEqualTo(RegistrarEstadoPagoEstadoPagoEnum.PAGADO);
        assertThat(response.idLiquidacion()).isEqualTo(ID_LIQUIDACION);
    }

    @Test
    void obtenerEstadoPagoPorLiquidacion_sinPago_lanzaExcepcion() {
        when(pagoRepository.findByIdLiquidacion(ID_LIQUIDACION)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.obtenerEstadoPagoPorLiquidacion(ID_LIQUIDACION))
                .isInstanceOf(RegistrarEstadoPagoPagoNoEncontradoException.class);
    }

    @Test
    void obtenerEventos_retornaListaOrdenada() {
        List<RegistrarEstadoPagoEventoTransaccion> eventos = List.of(
                eventoConEstado(EstadoEventoTransaccion.PROCESADO),
                eventoConEstado(EstadoEventoTransaccion.DUPLICADO)
        );
        when(eventoTransaccionRepository.findByIdPago(ID_PAGO)).thenReturn(eventos);

        var result = pagoService.obtenerEventos(ID_PAGO);

        assertThat(result).hasSize(2);
    }

    private EventoEstadoPagoRequestDTO buildDto(RegistrarEstadoPagoEstadoPagoEnum estado, Long secuencia) {
        return new EventoEstadoPagoRequestDTO(
                "evt-001", "txn-001", ID_PAGO, ID_LIQUIDACION,
                estado, LocalDateTime.now(), secuencia, null
        );
    }

    private RegistrarEstadoPagoPago pagoConEstado(RegistrarEstadoPagoEstadoPagoEnum estado, Long secuencia) {
        return new RegistrarEstadoPagoPago(ID_PAGO, null, null, Instant.now(), null, null,
                ID_LIQUIDACION, estado, Instant.now(), secuencia);
    }

    private RegistrarEstadoPagoEventoTransaccion eventoConEstado(EstadoEventoTransaccion estadoProc) {
        return new RegistrarEstadoPagoEventoTransaccion(UUID.randomUUID(), "txn-001", ID_PAGO, ID_LIQUIDACION,
                "{}", Instant.now(), Instant.now(), 1L,
                RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, estadoProc, null, true);
    }
}
