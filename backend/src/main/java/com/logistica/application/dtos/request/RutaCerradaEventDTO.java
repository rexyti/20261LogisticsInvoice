package com.logistica.application.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RutaCerradaEventDTO {
    @NotBlank

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
