package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.domain.models.Transportista;
import com.logistica.contratos.domain.repositories.TransportistaRepository;
import com.logistica.contratos.infrastructure.persistence.entities.TransportistaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransportistaRepositoryImpl implements TransportistaRepository {

    private final TransportistaJpaRepository jpaRepository;

    @Override
    public Transportista guardar(Transportista transportista) {
        TransportistaEntity entity = TransportistaEntity.builder()
                .idTransportista(transportista.getTransportistaId())
                .nombre(transportista.getNombre())
                .build();
        TransportistaEntity saved = jpaRepository.save(entity);
        return Transportista.builder()
                .transportistaId(saved.getIdTransportista())
                .nombre(saved.getNombre())
                .build();
    }

    @Override
    public Optional<Transportista> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(e -> Transportista.builder()
                        .transportistaId(e.getIdTransportista())
                        .nombre(e.getNombre())
                        .build());
    }
}
