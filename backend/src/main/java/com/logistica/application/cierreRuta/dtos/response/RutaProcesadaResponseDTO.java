package com.logistica.application.cierreRuta.dtos.response;

import com.logistica.domain.cierreRuta.enums.EstadoProcesamiento;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class RutaProcesadaResponseDTO {

    private UUID rutaId;
    private UUID vehiculoId;
    private String tipoVehiculo;
    private String modeloContrato;
    private EstadoProcesamiento estadoProcesamiento;
    private LocalDateTime fechaInicioTransito;
    private LocalDateTime fechaCierre;
    private TransportistaResponseDTO transportista;

    @Builder.Default
    private List<ParadaResponseDTO> paradas = List.of();
}
