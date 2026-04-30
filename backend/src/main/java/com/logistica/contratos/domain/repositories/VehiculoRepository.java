package com.logistica.contratos.domain.repositories;

import com.logistica.contratos.domain.models.Vehiculo;

import java.util.Optional;
import java.util.UUID;

public interface VehiculoRepository {
    Vehiculo guardar(Vehiculo vehiculo);
    Optional<Vehiculo> buscarPorId(UUID id);
}
