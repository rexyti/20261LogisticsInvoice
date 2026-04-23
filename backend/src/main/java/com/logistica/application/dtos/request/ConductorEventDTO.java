package com.logistica.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ConductorEventDTO {

    @NotNull
    private UUID conductorId;

    @NotBlank
    private String nombre;

    private String modeloContrato;
}
