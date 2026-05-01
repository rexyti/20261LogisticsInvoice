package com.logistica.NovedadEstadoPaquete.domain.repositories;

import com.logistica.NovedadEstadoPaquete.domain.models.HistorialEstado;

import java.util.List;

public interface HistorialRepository {

    HistorialEstado save(HistorialEstado historialEstado);

    List<HistorialEstado> findByIdPaquete(Long idPaquete, int page, int size);

    List<HistorialEstado> findByIdPaqueteOrderByFechaDesc(Long idPaquete);
}