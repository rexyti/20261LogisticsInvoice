package com.logistica.RegistrarEstadoPago.unit;

import com.logistica.RegistrarEstadoPago.application.dtos.request.EventoEstadoPagoRequestDTO;
import com.logistica.RegistrarEstadoPago.application.usecases.pago.ProcesarEventoPagoUseCase;
import com.logistica.RegistrarEstadoPago.domain.enums.RegistrarEstadoPagoEstadoPagoEnum;
import com.logistica.RegistrarEstadoPago.infrastructure.async.processors.EventoPagoProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoPagoProcessorTest {

    @Mock
    private ProcesarEventoPagoUseCase procesarEventoPagoUseCase;

    @InjectMocks
    private EventoPagoProcessor processor;

    @Test
    void procesarAsync_invocaCasoDeUso() {
        EventoEstadoPagoRequestDTO dto = new EventoEstadoPagoRequestDTO(
                "evt-001", "txn-001", UUID.randomUUID(), UUID.randomUUID(),
                RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, LocalDateTime.now(), 1L, null
        );

        processor.procesarAsync(dto);

        verify(procesarEventoPagoUseCase).procesarEvento(dto);
    }

    @Test
    void procesarAsync_capturaExcepcionSinPropagar() {
        EventoEstadoPagoRequestDTO dto = new EventoEstadoPagoRequestDTO(
                "evt-error", "txn-error", UUID.randomUUID(), UUID.randomUUID(),
                RegistrarEstadoPagoEstadoPagoEnum.EN_PROCESO, LocalDateTime.now(), 1L, null
        );
        doThrow(new RuntimeException("Error controlado")).when(procesarEventoPagoUseCase).procesarEvento(dto);

        processor.procesarAsync(dto);

        verify(procesarEventoPagoUseCase).procesarEvento(dto);
    }
}
