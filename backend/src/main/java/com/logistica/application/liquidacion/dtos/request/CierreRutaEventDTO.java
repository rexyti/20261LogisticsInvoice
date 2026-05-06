package com.logistica.application.liquidacion.dtos.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CierreRutaEventDTO {

    private UUID idRuta;

    @NotNull
    private UUID idContrato;

    @NotNull
    private OffsetDateTime fechaInicio;

    @NotNull
    private OffsetDateTime fechaCierre;

    @NotEmpty(message = "La lista de paquetes no puede estar vacía")
    private List<PaqueteDTO> paquetes;
}
