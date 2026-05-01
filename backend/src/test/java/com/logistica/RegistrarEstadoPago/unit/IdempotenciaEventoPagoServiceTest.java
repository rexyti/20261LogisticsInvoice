package com.logistica.RegistrarEstadoPago.unit;

import com.logistica.RegistrarEstadoPago.domain.enums.EstadoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.domain.models.RegistrarEstadoPagoEventoTransaccion;
import com.logistica.RegistrarEstadoPago.domain.repositories.EventoTransaccionRepository;
import com.logistica.RegistrarEstadoPago.domain.services.IdempotenciaEventoPagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotenciaEventoPagoServiceTest {

    @Mock
    private EventoTransaccionRepository eventoTransaccionRepository;

    private IdempotenciaEventoPagoService service;

    @BeforeEach
    void setUp() {
        service = new IdempotenciaEventoPagoService(eventoTransaccionRepository);
    }

    @Test
    void eventoYaProcesado_esDetectadoComoDuplicado() {
        RegistrarEstadoPagoEventoTransaccion eventoProcesado = eventoConEstado(EstadoEventoTransaccion.PROCESADO, true);
        when(eventoTransaccionRepository.findByIdTransaccionBanco("txn-001"))
                .thenReturn(Optional.of(eventoProcesado));

        assertThat(service.esEventoDuplicado("txn-001")).isTrue();
    }

    @Test
    void eventoNoExistente_noEsDuplicado() {
        when(eventoTransaccionRepository.findByIdTransaccionBanco("txn-nuevo"))
                .thenReturn(Optional.empty());

        assertThat(service.esEventoDuplicado("txn-nuevo")).isFalse();
    }

    @Test
    void eventoEnEstadoRecibido_noProcesado_noEsDuplicado() {
        RegistrarEstadoPagoEventoTransaccion eventoRecibido = eventoConEstado(EstadoEventoTransaccion.RECIBIDO, false);
        when(eventoTransaccionRepository.findByIdTransaccionBanco("txn-parcial"))
                .thenReturn(Optional.of(eventoRecibido));

        assertThat(service.esEventoDuplicado("txn-parcial")).isFalse();
    }

    private RegistrarEstadoPagoEventoTransaccion eventoConEstado(EstadoEventoTransaccion estado, boolean procesado) {
        return new RegistrarEstadoPagoEventoTransaccion(
                UUID.randomUUID(), "txn-001", UUID.randomUUID(), UUID.randomUUID(),
                "{}", Instant.now(), Instant.now(), 1L,
                RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, estado, null, procesado
        );
    }
}
