package com.logistica.domain.novedadEstadoPaquete.repositories;

import com.logistica.domain.novedadEstadoPaquete.models.LogSincronizacion;

import java.util.List;
import java.util.UUID;

public interface LogSincronizacionRepository {

    LogSincronizacion save(LogSincronizacion log);

    List<LogSincronizacion> findAll(int page, int size);

    List<LogSincronizacion> findByIdPaquete(UUID idPaquete, int page, int size);
}