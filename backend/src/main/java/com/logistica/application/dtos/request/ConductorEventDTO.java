package com.logistica.application.dtos.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ConductorEventDTO {

    @NotNull

    private UUID conductorId;

    @NotBlank
    private String nombre;

    private String modeloContrato;
}
