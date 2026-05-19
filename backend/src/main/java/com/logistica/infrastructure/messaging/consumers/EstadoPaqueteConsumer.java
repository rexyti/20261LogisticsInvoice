package com.logistica.infrastructure.messaging.consumers;

import com.logistica.application.novedadEstadoPaquete.ports.in.ProcesarEstadoPaquetePort;
import com.logistica.infrastructure.messaging.dtos.EstadoPaqueteMensajeDTO;
import com.logistica.infrastructure.messaging.mappers.EstadoPaqueteMensajeMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EstadoPaqueteConsumer {

    private final ProcesarEstadoPaquetePort procesarEstadoPaquetePort;
    private final EstadoPaqueteMensajeMapper mapper;

    @SqsListener("${app.sqs.queue.novedad-estado-paquete}")
    public void consumir(EstadoPaqueteMensajeDTO mensaje, Acknowledgement ack) {
        log.info("Evento ESTADO_PAQUETE recibido para id_paquete: {}", mensaje.getIdPaquete());

        try {
            procesarEstadoPaquetePort.ejecutar(mapper.toApplicationEvent(mensaje));
            ack.acknowledge();

        } catch (IllegalArgumentException e) {
            log.error("Error de validación no recuperable para paquete {}: {}",
                    mensaje.getIdPaquete(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Error procesando paquete {}. Se reintentará.", mensaje.getIdPaquete(), e);
            throw e;
        }
    }
}
