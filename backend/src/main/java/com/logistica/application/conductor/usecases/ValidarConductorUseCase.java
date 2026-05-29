package com.logistica.application.conductor.usecases;

import com.logistica.application.conductor.dtos.response.ConductorResponseDTO;
import com.logistica.domain.conductor.exceptions.ConductorInactivoException;
import com.logistica.domain.conductor.models.Conductor;
import com.logistica.domain.conductor.ports.ConductorGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidarConductorUseCase {

    private final ConductorGateway conductorGateway;

    public ConductorResponseDTO ejecutar(UUID conductorId) {
        log.info("Validando conductor_id: {}", conductorId);

        Conductor conductor = conductorGateway.consultar(conductorId);

        if (!conductor.estaActivo()) {
            log.warn("Conductor {} rechazado por estado: {}", conductorId, conductor.getEstado());
            throw new ConductorInactivoException(conductorId);
        }

        log.info("Conductor {} validado correctamente, nombre: {}", conductorId, conductor.getNombre());

        return ConductorResponseDTO.builder()
                .conductorId(conductor.getConductorId())
                .nombre(conductor.getNombre())
                .estado(conductor.getEstado())
                .build();
    }
}
