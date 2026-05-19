package com.logistica.infrastructure.messaging.consumers;

import com.logistica.application.contratos.ports.in.ProcesarContratoCreadoPort;
import com.logistica.infrastructure.messaging.dtos.ContratoCreadoMensajeDTO;
import com.logistica.infrastructure.messaging.mappers.ContratoCreadoMensajeMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContratoCreadoConsumer {

    private final ProcesarContratoCreadoPort procesarContratoCreadoPort;
    private final ContratoCreadoMensajeMapper mapper;

    @SqsListener("${app.sqs.queue.contrato-creado}")
    public void consumir(ContratoCreadoMensajeDTO mensaje, Acknowledgement ack) {
        log.info("Evento CONTRATO_CREADO recibido para id_contrato: {}", mensaje.getIdContrato());

        try {
            procesarContratoCreadoPort.ejecutar(mapper.toApplicationEvent(mensaje));
            ack.acknowledge();

        } catch (IllegalArgumentException e) {
            log.error("Error de validación no recuperable para contrato {}: {}",
                    mensaje.getIdContrato(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Error procesando contrato {}. Se reintentará.", mensaje.getIdContrato(), e);
            throw e;
        }
    }
}
