package com.logistica.infrastructure.contratos.persistence.repositories;

import com.logistica.domain.contratos.models.Transportista;
import com.logistica.domain.contratos.repositories.TransportistaRepository;
import com.logistica.infrastructure.contratos.persistence.entities.TransportistaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ContratoTransportistaRepositoryImpl implements TransportistaRepository {

    private final ContratoTransportistaJpaRepository jpaRepository;

    @Override
    public Transportista guardar(Transportista transportista) {
        TransportistaEntity entity = TransportistaEntity.builder()
                .id(transportista.getTransportistaId())
                .nombre(transportista.getNombre())
                .build();
        TransportistaEntity saved = jpaRepository.save(entity);
        return Transportista.builder()
                .transportistaId(saved.getId())
                .nombre(saved.getNombre())
                .build();
    }

    @Override
    public Optional<Transportista> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(e -> Transportista.builder()
                        .transportistaId(e.getId())
                        .nombre(e.getNombre())
                        .build());
    }
}
