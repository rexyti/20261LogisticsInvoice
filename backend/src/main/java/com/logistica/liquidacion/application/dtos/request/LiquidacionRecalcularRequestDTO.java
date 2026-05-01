package com.logistica.liquidacion.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class LiquidacionRecalcularRequestDTO {

    @NotEmpty
    private List<LiquidacionAjusteDTO> ajustes;

    @NotBlank
    private String responsable;
}
