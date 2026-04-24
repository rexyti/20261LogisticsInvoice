package com.logistica.domain.repositories;

import com.logistica.domain.models.HistorialEstado;

import java.util.List;

public interface HistorialRepository {

    HistorialEstado save(HistorialEstado historial);

    List<HistorialEstado> findByIdPaqueteOrderByFechaDesc(Long idPaquete);
}
