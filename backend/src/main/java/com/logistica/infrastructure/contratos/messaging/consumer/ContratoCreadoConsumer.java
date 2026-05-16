package com.logistica.infrastructure.contratos.messaging.consumer;

import com.logistica.application.contratos.dtos.request.ContratoCreadoEvent;
import com.logistica.application.contratos.usecases.contrato.ProcesarContratoCreadoUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContratoCreadoConsumer {

    private final ProcesarContratoCreadoUseCase useCase;

    @SqsListener("${app.sqs.queue.contrato-creado}")
    public void consumir(ContratoCreadoEvent evento, Acknowledgement ack) {
        log.info("Evento CONTRATO_CREADO recibido para contrato: {}", evento.getIdContrato());

        try {
            useCase.ejecutar(evento);
            ack.acknowledge();

        } catch (IllegalArgumentException e) {
            log.error("Error de validación no recuperable en contrato {}: {}",
                    evento.getIdContrato(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Error procesando contrato {}. Se reintentará.", evento.getIdContrato(), e);
            throw e; // SQS reintenta automáticamente
        }
    }
}
