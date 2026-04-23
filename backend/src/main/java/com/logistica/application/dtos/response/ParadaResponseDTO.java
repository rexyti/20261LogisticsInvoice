package com.logistica.application.dtos.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ParadaResponseDTO {
    private UUID paradaId;
    private String estado;
    private String motivoFalla;
    private String responsable;
}
