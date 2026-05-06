package com.logistica.application.contratos.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SeguroResponseDTO {
    private UUID id;
    private String numeroPoliza;
    private String estado;
}