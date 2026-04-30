package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.domain.enums.TipoVehiculo;
import com.logistica.contratos.domain.models.Vehiculo;
import com.logistica.contratos.domain.repositories.VehiculoRepository;
import com.logistica.contratos.infrastructure.persistence.entities.VehiculoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VehiculoRepositoryImpl implements VehiculoRepository {

    private final VehiculoJpaRepository jpaRepository;

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        VehiculoEntity entity = VehiculoEntity.builder()
                .tipo(vehiculo.getTipo().name())
                .build();
        VehiculoEntity saved = jpaRepository.save(entity);
        return Vehiculo.builder()
                .idVehiculo(saved.getIdVehiculo())
                .tipo(TipoVehiculo.valueOf(saved.getTipo()))
                .build();
    }

    @Override
    public Optional<Vehiculo> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(e -> Vehiculo.builder()
                        .idVehiculo(e.getIdVehiculo())
                        .tipo(TipoVehiculo.valueOf(e.getTipo()))
                        .build());
    }
}
