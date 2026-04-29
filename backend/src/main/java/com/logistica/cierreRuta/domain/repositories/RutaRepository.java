package com.logistica.cierreRuta.domain.repositories;

import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.domain.models.Ruta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RutaRepository {
    boolean existsByRutaId(UUID rutaId);
    Ruta guardar(Ruta ruta);
    Optional<Ruta> buscarPorRutaId(UUID rutaId);
    Page<Ruta> listarTodas( Pageable pageable);
    Page<Ruta> buscarPorEstado(EstadoProcesamiento estado, Pageable pageable);
}
