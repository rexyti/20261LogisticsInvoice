package com.logistica.liquidacion.domain.repositories;

import com.logistica.liquidacion.domain.models.AuditoriaLiquidacion;
import java.util.List;
import java.util.UUID;

public interface AuditoriaLiquidacionRepository {
    AuditoriaLiquidacion save(AuditoriaLiquidacion auditoriaLiquidacion);
    List<AuditoriaLiquidacion> findByIdLiquidacion(UUID liquidacionId);
}
