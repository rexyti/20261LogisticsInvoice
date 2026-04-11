package com.logistica.domain.repositories;

import com.logistica.domain.models.Ajuste;
import java.util.List;
import java.util.UUID;

public interface AjusteRepository {
    Ajuste save(Ajuste ajuste);
    List<Ajuste> saveAll(List<Ajuste> ajustes);
    List<Ajuste> findByLiquidacionId(UUID liquidacionId);
}
