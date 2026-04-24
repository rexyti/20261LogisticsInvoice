package com.logistica.domain.repositories;

import com.logistica.domain.models.HistorialEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HistorialRepository {
    HistorialEstado save(HistorialEstado historial);
    Page<HistorialEstado> findByIdPaquete(UUID idPaquete, Pageable pageable);
}
