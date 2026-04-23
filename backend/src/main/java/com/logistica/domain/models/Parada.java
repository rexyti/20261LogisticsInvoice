package com.logistica.domain.models;

import com.logistica.domain.enums.EstadoParada;
import com.logistica.domain.enums.MotivoFalla;
import com.logistica.domain.enums.ResponsableFalla;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Parada {
    private UUID paradaId;
    private EstadoParada estado;
    private MotivoFalla motivoFalla;
    private ResponsableFalla responsable;
}
