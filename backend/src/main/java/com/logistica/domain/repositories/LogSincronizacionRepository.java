package com.logistica.domain.repositories;

import com.logistica.domain.models.LogSincronizacion;

import java.util.List;
import java.util.UUID;

public interface LogSincronizacionRepository {
    LogSincronizacion save(LogSincronizacion log);
    List<LogSincronizacion> findByIdPaquete(UUID idPaquete);
}
