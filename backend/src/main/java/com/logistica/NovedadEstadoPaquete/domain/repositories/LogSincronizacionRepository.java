package com.logistica.NovedadEstadoPaquete.domain.repositories;

import com.logistica.NovedadEstadoPaquete.domain.models.LogSincronizacion;

import java.util.List;

public interface LogSincronizacionRepository {

    LogSincronizacion save(LogSincronizacion log);

    List<LogSincronizacion> findAll(int page, int size);

    List<LogSincronizacion> findByIdPaquete(Long idPaquete, int page, int size);
}