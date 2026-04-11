package com.logistica.domain.repositories;

import com.logistica.domain.models.Liquidacion;
import java.util.Optional;
import java.util.UUID;

public interface LiquidacionRepository {
    Liquidacion save(Liquidacion liquidacion);
    Optional<Liquidacion> findById(UUID id);
    boolean existsByIdRuta(UUID idRuta);
}
