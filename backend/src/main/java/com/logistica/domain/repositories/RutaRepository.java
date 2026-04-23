package com.logistica.domain.repositories;

import com.logistica.domain.models.Ruta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RutaRepository {
    boolean existsByRutaId(UUID rutaId);
    Ruta guardar(Ruta ruta);
    Optional<Ruta> buscarPorRutaId(UUID rutaId);
    List<Ruta> listarTodas();
}
