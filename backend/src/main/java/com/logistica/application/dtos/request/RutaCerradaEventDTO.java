package com.logistica.application.dtos.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RutaCerradaEventDTO {

    @NotBlank
    @Pattern(regexp = "RUTA_CERRADA", message = "Tipo de evento no soportado: debe ser RUTA_CERRADA")
    private String tipoEvento;

    @NotNull

    private UUID rutaId;

    @NotNull

    private LocalDateTime fechaHoraInicioTransito;

    @NotNull

    private LocalDateTime fechaHoraCierre;

    @Valid
    @NotNull
    private ConductorEventDTO conductor;

    @Valid
    @NotNull
    private VehiculoEventDTO vehiculo;

    @Valid
    @NotEmpty
    private List<ParadaEventDTO> paradas;
}
