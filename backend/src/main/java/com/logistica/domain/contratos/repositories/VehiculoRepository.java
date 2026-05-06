package com.logistica.domain.contratos.repositories;

import com.logistica.domain.contratos.models.Vehiculo;

import java.util.Optional;
import java.util.UUID;

public interface VehiculoRepository {
    Vehiculo guardar(Vehiculo vehiculo);
    Optional<Vehiculo> buscarPorId(UUID id);
}
