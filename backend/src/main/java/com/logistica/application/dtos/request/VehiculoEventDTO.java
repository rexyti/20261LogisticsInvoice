package com.logistica.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class VehiculoEventDTO {
    @NotNull
    private UUID vehiculoId;

    @NotBlank
    private String tipo;
}
