package com.logistica.domain.repositories;

import com.logistica.domain.models.Vehiculo;

import java.util.Optional;

public interface VehiculoRepository {
    Vehiculo guardar(Vehiculo vehiculo);
    Optional<Vehiculo> buscarPorId(Long id);
}
