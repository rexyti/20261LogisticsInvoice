package com.logistica.domain.liquidacion.repositories;

import com.logistica.domain.liquidacion.models.Liquidacion;
import java.util.Optional;
import java.util.UUID;

public interface LiquidacionRepository {
    Liquidacion save(Liquidacion liquidacion);
    Optional<Liquidacion> findById(UUID id);
    Optional<Liquidacion> findByIdRuta(UUID idRuta);
    boolean existsByIdRuta(UUID idRuta);
}
