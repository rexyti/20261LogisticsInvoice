package com.logistica.domain.cierreRuta.repositories;

import com.logistica.domain.cierreRuta.enums.EstadoProcesamiento;
import com.logistica.domain.cierreRuta.models.RutaCerrada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RutaRepository {
    boolean existsByRutaId(UUID rutaId);
    RutaCerrada guardar(RutaCerrada ruta);
    Optional<RutaCerrada> buscarPorRutaId(UUID rutaId);
    Page<RutaCerrada> listarTodas( Pageable pageable);
    Page<RutaCerrada> buscarPorEstado(EstadoProcesamiento estado, Pageable pageable);
}
