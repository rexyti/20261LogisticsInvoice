package com.logistica.contratos.application.dtos.response;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class SeguroResponseDTO {
    private UUID id;
    private String numeroPoliza;
    private String estado;
}