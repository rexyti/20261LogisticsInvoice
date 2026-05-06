package com.logistica.domain.novedadEstadoPaquete.repositories;

import com.logistica.domain.novedadEstadoPaquete.models.HistorialEstado;

import java.util.List;

public interface HistorialRepository {

    HistorialEstado save(HistorialEstado historialEstado);

    List<HistorialEstado> findByIdPaquete(Long idPaquete, int page, int size);
}