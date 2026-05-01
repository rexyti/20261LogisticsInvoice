package com.logistica.contratos.infrastructure.persistence.repositories;

import com.logistica.contratos.domain.models.ContratosTransportista;
import com.logistica.contratos.domain.repositories.ContratosTransportistaRepository;
import com.logistica.contratos.infrastructure.persistence.entities.ContratosTransportistaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ContratosTransportistaRepositoryImpl implements ContratosTransportistaRepository {

    private final ContratosTransportistaJpaRepository jpaRepository;

    @Override
    public ContratosTransportista guardar(ContratosTransportista transportista) {
        ContratosTransportistaEntity entity = ContratosTransportistaEntity.builder()
                .idTransportista(transportista.getTransportistaId())
                .nombre(transportista.getNombre())
                .build();
        ContratosTransportistaEntity saved = jpaRepository.save(entity);
        return ContratosTransportista.builder()
                .transportistaId(saved.getIdTransportista())
                .nombre(saved.getNombre())
                .build();
    }

    @Override
    public Optional<ContratosTransportista> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(e -> ContratosTransportista.builder()
                        .transportistaId(e.getIdTransportista())
                        .nombre(e.getNombre())
                        .build());
    }
}
