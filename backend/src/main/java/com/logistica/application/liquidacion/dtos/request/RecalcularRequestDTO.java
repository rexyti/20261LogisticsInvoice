package com.logistica.application.liquidacion.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RecalcularRequestDTO {

    @NotEmpty
    private List<AjusteDTO> ajustes;

    @NotBlank
    private String responsable;
}
