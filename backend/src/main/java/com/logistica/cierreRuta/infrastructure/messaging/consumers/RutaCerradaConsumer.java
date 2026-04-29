package com.logistica.cierreRuta.infrastructure.messaging.consumers;

import com.logistica.cierreRuta.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.cierreRuta.application.usecases.ruta.ProcesarRutaCerradaUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RutaCerradaConsumer {

    private final ProcesarRutaCerradaUseCase useCase;

    @SqsListener("${app.sqs.queue.ruta-cerrada}")
    public void consumir(RutaCerradaEventDTO evento,
                         Acknowledgement ack) {

        log.info("Evento RUTA_CERRADA recibido para ruta_id: {}", evento.getRutaId());

        try {
            useCase.ejecutar(evento);

            ack.acknowledge();

        } catch (IllegalArgumentException e) {

            log.error("Error de validación no recuperable en ruta {}: {}",
                    evento.getRutaId(), e.getMessage());
            throw e;

        } catch (Exception e) {

            log.error("Error procesando ruta {}. Se reintentará.", evento.getRutaId(), e);

            throw e; // SQS reintenta automáticamente
        }
    }
}