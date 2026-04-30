package com.logistica.contratos.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeguroRequestDTO {

    @NotBlank(message = "numeroPoliza es obligatorio")
    private String numeroPoliza;

    @NotBlank(message = "estado es obligatorio")
    private String estado;

}
