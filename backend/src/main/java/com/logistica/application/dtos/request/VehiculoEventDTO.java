package com.logistica.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class VehiculoEventDTO {
    @NotNull
    private UUID vehiculoId;

    @NotBlank
    private String tipo;
}
