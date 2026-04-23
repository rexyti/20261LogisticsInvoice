package com.logistica.application.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class RutaProcesadaResponseDTO {

    private UUID rutaId;

    private String tipoVehiculo;
    private String modeloContrato;
    private String estadoProcesamiento;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicioTransito;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCierre;

    private TransportistaResponseDTO transportista;

    @Builder.Default
    private List<ParadaResponseDTO> paradas = List.of();
}
