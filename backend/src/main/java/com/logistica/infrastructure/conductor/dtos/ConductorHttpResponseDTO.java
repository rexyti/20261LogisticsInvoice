package com.logistica.infrastructure.conductor.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ConductorHttpResponseDTO {

    @JsonProperty("conductor_id")
    private UUID conductorId;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("estado")
    private String estado;
}
