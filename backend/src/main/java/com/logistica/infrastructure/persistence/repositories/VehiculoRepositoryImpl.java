package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.Vehiculo;
import com.logistica.domain.repositories.VehiculoRepository;
import com.logistica.infrastructure.persistence.entities.UsuarioEntity;
import com.logistica.infrastructure.persistence.entities.VehiculoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VehiculoRepositoryImpl implements VehiculoRepository {

    private final VehiculoJpaRepository jpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        UsuarioEntity usuario = usuarioJpaRepository.getReferenceById(vehiculo.getIdUsuario());
        VehiculoEntity entity = VehiculoEntity.builder()
                .usuario(usuario)
                .tipo(vehiculo.getTipo())
                .build();
        VehiculoEntity saved = jpaRepository.save(entity);
        return Vehiculo.builder()
                .idVehiculo(saved.getIdVehiculo())
                .idUsuario(saved.getUsuario().getIdUsuario())
                .tipo(saved.getTipo())
                .build();
    }

    @Override
    public Optional<Vehiculo> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(e -> Vehiculo.builder()
                        .idVehiculo(e.getIdVehiculo())
                        .tipo(e.getTipo())
                        .build());
    }
}
