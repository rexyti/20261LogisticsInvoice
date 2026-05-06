package com.logistica.application.liquidacion.dtos.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistica.domain.liquidacion.enums.EstadoPaquete;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaqueteDTO {

    @NotNull
    private UUID id;

    @NotNull
    private EstadoPaquete estadoFinal;

    private String novedades;
}
