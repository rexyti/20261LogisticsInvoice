package com.logistica.infrastructure.conductor.adapters;

import com.logistica.domain.conductor.enums.EstadoConductor;
import com.logistica.domain.conductor.exceptions.ConductorNoEncontradoException;
import com.logistica.domain.conductor.models.Conductor;
import com.logistica.domain.conductor.ports.ConductorGateway;
import com.logistica.infrastructure.conductor.dtos.ConductorHttpResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Slf4j
@Component
public class ConductorGatewayImpl implements ConductorGateway {

    private final RestClient conductorRestClient;

    public ConductorGatewayImpl(@Qualifier("conductorRestClient") RestClient conductorRestClient) {
        this.conductorRestClient = conductorRestClient;
    }

    @Override
    public Conductor consultar(UUID conductorId) {
        log.info("Consultando servicio externo de conductores. conductor_id: {}", conductorId);

        ConductorHttpResponseDTO response;
        try {
            response = conductorRestClient.get()
                    .uri("/conductores/{id}", conductorId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.warn("Conductor no encontrado en servicio externo. conductor_id: {}", conductorId);
                        throw new ConductorNoEncontradoException(conductorId);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("Error interno del servicio de conductores. conductor_id: {}", conductorId);
                        throw new RuntimeException("El servicio externo de conductores respondió con error interno");
                    })
                    .body(ConductorHttpResponseDTO.class);
        } catch (ConductorNoEncontradoException ex) {
            throw ex;
        } catch (RestClientException ex) {
            log.error("Error de comunicación con el servicio de conductores. conductor_id: {}", conductorId, ex);
            throw new RuntimeException("No se pudo conectar al servicio de conductores: " + ex.getMessage(), ex);
        }

        if (response == null) {
            throw new ConductorNoEncontradoException(conductorId);
        }

        log.info("Respuesta recibida del servicio de conductores. conductor_id: {}, estado: {}",
                conductorId, response.getEstado());

        return Conductor.builder()
                .conductorId(response.getConductorId())
                .nombre(response.getNombre())
                .estado(EstadoConductor.valueOf(response.getEstado()))
                .build();
    }
}
