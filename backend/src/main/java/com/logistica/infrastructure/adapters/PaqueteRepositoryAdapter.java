package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.Paquete;
import com.logistica.domain.repositories.PaqueteRepository;
import com.logistica.infrastructure.persistence.entities.PaqueteEntity;
import com.logistica.infrastructure.persistence.repositories.PaqueteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaqueteRepositoryAdapter implements PaqueteRepository {

    private final PaqueteJpaRepository jpaRepository;
    private final PaqueteMapper mapper;

    @Override
    public Optional<Paquete> findById(UUID idPaquete) {
        return jpaRepository.findById(idPaquete).map(mapper::toDomain);
    }

    @Override
    public Paquete save(Paquete paquete) {
        PaqueteEntity entity = jpaRepository.findById(paquete.idPaquete())
                .orElseGet(() -> mapper.toEntity(paquete));

        entity.setIdRuta(paquete.idRuta());
        entity.setEstadoActual(paquete.estadoActual());

        return mapper.toDomain(jpaRepository.save(entity));
    }
}
