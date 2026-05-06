package com.logistica.application.cierreRuta.dtos.response;

import com.logistica.domain.cierreRuta.enums.EstadoParada;
import com.logistica.domain.cierreRuta.enums.MotivoFalla;
import com.logistica.domain.cierreRuta.enums.ResponsableFalla;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ParadaResponseDTO {
    private UUID paradaId;
    private UUID paqueteId;
    private EstadoParada estado;
    private MotivoFalla motivoFalla;
    private ResponsableFalla responsable;
}
