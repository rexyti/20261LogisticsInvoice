package com.logistica.cierreRuta.infrastructure.persistence.repositories;

import com.logistica.cierreRuta.domain.models.Transportista;
import com.logistica.cierreRuta.domain.repositories.TransportistaRepository;
import com.logistica.cierreRuta.infrastructure.adapters.TransportistaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransportistaRepositoryImpl implements TransportistaRepository {

    private final TransportistaJpaRepository jpaRepository;
    private final TransportistaMapper mapper;

    @Override
    public Optional<Transportista> buscarPorTransportistaId(UUID transportistaId) {
        return jpaRepository.findByConductorId(transportistaId)
                .map(mapper::toDomain);
    }

    @Override
    public Transportista guardar(Transportista transportista) {
        var entity = mapper.toEntity(transportista);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
