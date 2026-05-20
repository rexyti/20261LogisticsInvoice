package com.logistica.application.conductor.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistica.domain.conductor.enums.EstadoConductor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ConductorResponseDTO {

    private final UUID conductorId;
    private final String nombre;
    private final EstadoConductor estado;
}
