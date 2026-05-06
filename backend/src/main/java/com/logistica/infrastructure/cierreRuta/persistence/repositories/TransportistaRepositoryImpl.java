package com.logistica.infrastructure.cierreRuta.persistence.repositories;

import com.logistica.domain.cierreRuta.models.TransportistaRuta;
import com.logistica.domain.cierreRuta.repositories.TransportistaRutaRepository;
import com.logistica.infrastructure.cierreRuta.adapters.TransportistaMapper;
import com.logistica.infrastructure.contratos.persistence.repositories.ContratoTransportistaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransportistaRepositoryImpl implements TransportistaRutaRepository {

    private final ContratoTransportistaJpaRepository jpaRepository;
    private final TransportistaMapper mapper;

    @Override
    public Optional<TransportistaRuta> buscarPorTransportistaId(UUID transportistaId) {
        return jpaRepository.findById(transportistaId)
                .map(mapper::toDomain);
    }

    @Override
    public TransportistaRuta guardar(TransportistaRuta transportista) {
        var entity = mapper.toEntity(transportista);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
