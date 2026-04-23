package com.logistica.infrastructure.messaging.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.application.dtos.request.RutaCerradaEventDTO;
import com.logistica.application.usecases.ruta.ProcesarRutaCerradaUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RutaCerradaConsumer {

    private final ProcesarRutaCerradaUseCase procesarRutaCerradaUseCase;
    private final ObjectMapper objectMapper;

    @SqsListener("${app.sqs.queue.ruta-cerrada}")
    public void consumir(String mensaje) {
        try {
            RutaCerradaEventDTO evento = objectMapper.readValue(mensaje, RutaCerradaEventDTO.class);
            log.info("Evento RUTA_CERRADA recibido para ruta_id: {}", evento.getRutaId());
            procesarRutaCerradaUseCase.ejecutar(evento);
        } catch (JsonProcessingException e) {
            log.error("JSON malformado en evento RUTA_CERRADA — enviando a DLQ: {}", e.getMessage());
            throw new RuntimeException("JSON inválido", e);
        }
    }
}
