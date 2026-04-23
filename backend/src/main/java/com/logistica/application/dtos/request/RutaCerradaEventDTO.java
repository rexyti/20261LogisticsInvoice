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
    @JsonProperty("tipo_evento")
    private String tipoEvento;

    @NotNull
    @JsonProperty("ruta_id")
    private UUID rutaId;

    @NotNull
    @JsonProperty("fecha_hora_inicio_transito")
    private LocalDateTime fechaHoraInicioTransito;

    @NotNull
    @JsonProperty("fecha_hora_cierre")
    @JsonFormat(pattern = "yyyy-MM-dd 'T'HH:mm:ss")
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
