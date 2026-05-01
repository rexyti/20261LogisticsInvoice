package com.logistica.cierreRuta.infrastructure.persistence.repositories;

import com.logistica.cierreRuta.domain.models.CierreRutaTransportista;
import com.logistica.cierreRuta.domain.repositories.CierreRutaTransportistaRepository;
import com.logistica.cierreRuta.infrastructure.adapters.CierreRutaTransportistaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CierreRutaTransportistaRepositoryImpl implements CierreRutaTransportistaRepository {

    private final CierreRutaTransportistaJpaRepository jpaRepository;
    private final CierreRutaTransportistaMapper mapper;

    @Override
    public Optional<CierreRutaTransportista> buscarPorTransportistaId(UUID transportistaId) {
        return jpaRepository.findByConductorId(transportistaId)
                .map(mapper::toDomain);
    }

    @Override
    public CierreRutaTransportista guardar(CierreRutaTransportista transportista) {
        var entity = mapper.toEntity(transportista);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
