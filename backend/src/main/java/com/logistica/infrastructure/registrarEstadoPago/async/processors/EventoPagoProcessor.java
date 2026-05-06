package com.logistica.infrastructure.registrarEstadoPago.async.processors;

import com.logistica.application.registrarEstadoPago.dtos.request.EventoEstadoPagoRequestDTO;
import com.logistica.application.registrarEstadoPago.ports.EventoPagoAsyncPort;
import com.logistica.application.registrarEstadoPago.usecases.pago.ProcesarEventoPagoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventoPagoProcessor implements EventoPagoAsyncPort {

    private final ProcesarEventoPagoUseCase procesarEventoPagoUseCase;

    @Override
    @Async("asyncPagoExecutor")
    public void procesarAsync(EventoEstadoPagoRequestDTO dto) {
        log.info("Procesando evento de pago de forma asíncrona: idEvento={}, idTransaccionBanco={}",
                dto.idEvento(), dto.idTransaccionBanco());
        try {
            procesarEventoPagoUseCase.procesarEvento(dto);
            log.info("Evento procesado exitosamente: {}", dto.idEvento());
        } catch (Exception ex) {
            log.error("Error al procesar evento de pago: idEvento={}, error={}",
                    dto.idEvento(), ex.getMessage(), ex);
        }
    }
}
