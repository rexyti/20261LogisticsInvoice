package com.logistica.application.dtos.response;

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
    private LocalDateTime fechaInicioTransito;
    private LocalDateTime fechaCierre;
    private TransportistaResponseDTO transportista;
    private List<ParadaResponseDTO> paradas;
}
