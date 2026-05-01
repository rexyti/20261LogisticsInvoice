package com.logistica.liquidacion.application.dtos.request;

import com.logistica.liquidacion.domain.enums.LiquidacionEstadoPaquete;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class LiquidacionPaqueteDTO {

    @NotNull
    private UUID id;

    @NotNull
    private LiquidacionEstadoPaquete estadoFinal;

    private String novedades;
}
