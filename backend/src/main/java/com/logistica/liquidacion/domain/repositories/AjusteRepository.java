package com.logistica.liquidacion.domain.repositories;

import com.logistica.liquidacion.domain.models.Ajuste;
import java.util.List;
import java.util.UUID;

public interface AjusteRepository {
    Ajuste save(Ajuste ajuste);
    List<Ajuste> saveAll(List<Ajuste> ajustes);
    List<Ajuste> findByIdLiquidacion(UUID liquidacionId);
}
