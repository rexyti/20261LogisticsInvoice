package com.logistica.liquidacion.application.dtos.response;

import com.logistica.liquidacion.domain.enums.TipoAjuste;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class AjusteResponseDTO {
    private final UUID id;
    private final UUID idLiquidacion;
    private final TipoAjuste tipo;
    private final BigDecimal monto;
    private final String motivo;
    private final OffsetDateTime creadoEn;
}
