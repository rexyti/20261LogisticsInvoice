package com.logistica.domain.liquidacion.repositories;

import com.logistica.domain.liquidacion.models.Ajuste;
import java.util.List;
import java.util.UUID;

public interface AjusteRepository {
    Ajuste save(Ajuste ajuste);
    List<Ajuste> saveAll(List<Ajuste> ajustes);
    List<Ajuste> findByIdLiquidacion(UUID liquidacionId);
}
