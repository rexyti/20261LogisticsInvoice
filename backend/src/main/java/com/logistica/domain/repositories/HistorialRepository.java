package com.logistica.domain.repositories;

import com.logistica.domain.models.HistorialEstado;

import java.util.List;

public interface HistorialRepository {

    HistorialEstado save(HistorialEstado historialEstado);

    List<HistorialEstado> findByIdPaquete(Long idPaquete, int page, int size);

    List<HistorialEstado> findByIdPaqueteOrderByFechaDesc(Long idPaquete);
}