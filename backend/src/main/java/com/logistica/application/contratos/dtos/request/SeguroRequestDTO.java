package com.logistica.application.contratos.dtos.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SeguroRequestDTO {

    @NotBlank(message = "numeroPoliza es obligatorio")
    private String numeroPoliza;

    @NotBlank(message = "estado es obligatorio")
    private String estado;

}
