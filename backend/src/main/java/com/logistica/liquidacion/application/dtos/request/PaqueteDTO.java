package com.logistica.liquidacion.application.dtos.request;

import com.logistica.liquidacion.domain.enums.EstadoPaquete;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class PaqueteDTO {

    @NotNull
    private UUID id;

    @NotNull
    private EstadoPaquete estadoFinal;

    private String novedades;
}