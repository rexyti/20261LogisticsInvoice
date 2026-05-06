package com.logistica.application.liquidacion.dtos.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
import com.logistica.domain.liquidacion.enums.TipoAjuste;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AjusteDTO {

    private UUID id;

    @NotNull(message = "El tipo de ajuste es obligatorio")
    private TipoAjuste tipo;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;
}
