package com.logistica.infrastructure.novedadEstadoPaquete.messaging.consumer;

import com.logistica.application.novedadEstadoPaquete.dtos.request.NovedadEstadoPaqueteEvent;
import com.logistica.application.novedadEstadoPaquete.usecases.paquete.ProcesarNovedadEstadoPaqueteUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NovedadEstadoPaqueteConsumer {

    private final ProcesarNovedadEstadoPaqueteUseCase useCase;

    @SqsListener("${app.sqs.queue.novedad-estado-paquete}")
    public void consumir(NovedadEstadoPaqueteEvent evento, Acknowledgement ack) {
        log.info("Evento NOVEDAD_ESTADO_PAQUETE recibido para paquete: {}", evento.getIdPaquete());

        try {
            useCase.ejecutar(evento);
            ack.acknowledge();

        } catch (IllegalArgumentException e) {
            log.error("Error de validación no recuperable para paquete {}: {}",
                    evento.getIdPaquete(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Error procesando paquete {}. Se reintentará.", evento.getIdPaquete(), e);
            throw e;
        }
    }
}
