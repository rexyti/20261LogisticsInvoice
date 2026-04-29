package com.logistica.cierreRuta.domain.repositories;

import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.domain.models.CierreRutaRuta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RutaRepository {
    boolean existsByRutaId(UUID rutaId);
    CierreRutaRuta guardar(CierreRutaRuta ruta);
    Optional<CierreRutaRuta> buscarPorRutaId(UUID rutaId);
    Page<CierreRutaRuta> listarTodas( Pageable pageable);
    Page<CierreRutaRuta> buscarPorEstado(EstadoProcesamiento estado, Pageable pageable);
}
