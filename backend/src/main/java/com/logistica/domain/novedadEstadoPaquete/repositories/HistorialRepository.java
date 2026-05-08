package com.logistica.domain.novedadEstadoPaquete.repositories;

import com.logistica.domain.novedadEstadoPaquete.models.HistorialEstado;

import java.util.List;
import java.util.UUID;

public interface HistorialRepository {

    HistorialEstado save(HistorialEstado historialEstado);

    List<HistorialEstado> findByIdPaquete(UUID idPaquete, int page, int size);
}