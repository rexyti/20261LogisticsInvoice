package com.logistica.application.liquidacion.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.logistica.domain.liquidacion.enums.TipoAjuste;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AjusteResponseDTO {
    private final UUID id;
    private final UUID idLiquidacion;
    private final TipoAjuste tipo;
    private final BigDecimal monto;
    private final String motivo;
    private final OffsetDateTime creadoEn;
}
