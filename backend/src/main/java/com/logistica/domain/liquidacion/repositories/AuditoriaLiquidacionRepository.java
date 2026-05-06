package com.logistica.domain.liquidacion.repositories;

import com.logistica.domain.liquidacion.models.AuditoriaLiquidacion;
import java.util.List;
import java.util.UUID;

public interface AuditoriaLiquidacionRepository {
    AuditoriaLiquidacion save(AuditoriaLiquidacion auditoriaLiquidacion);
    List<AuditoriaLiquidacion> findByIdLiquidacion(UUID liquidacionId);
}
