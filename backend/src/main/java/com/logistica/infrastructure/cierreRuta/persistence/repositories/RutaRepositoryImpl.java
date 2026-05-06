package com.logistica.infrastructure.cierreRuta.persistence.repositories;

import com.logistica.domain.cierreRuta.enums.EstadoProcesamiento;
import com.logistica.domain.cierreRuta.models.RutaCerrada;
import com.logistica.domain.cierreRuta.repositories.RutaRepository;
import com.logistica.infrastructure.cierreRuta.adapters.RutaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RutaRepositoryImpl implements RutaRepository {

    private final RutaJpaRepository jpaRepository;
    private final RutaMapper rutaMapper;

    @Override
    public boolean existsByRutaId(UUID rutaId) {
        return jpaRepository.existsByRutaId(rutaId);
    }

    @Override
    public RutaCerrada guardar(RutaCerrada ruta) {
        var entity = rutaMapper.toEntity(ruta);
        var saved = jpaRepository.save(entity);
        return rutaMapper.toDomain(saved);
    }

    @Override
    public Optional<RutaCerrada> buscarPorRutaId(UUID rutaId) {
        return jpaRepository.findByRutaId(rutaId).map(rutaMapper::toDomain);
    }

    @Override
    public Page<RutaCerrada> listarTodas(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(rutaMapper::toDomain);
    }

    @Override
    public Page<RutaCerrada> buscarPorEstado(EstadoProcesamiento estado, Pageable pageable) {
        return jpaRepository.findByEstadoProcesamiento(estado, pageable)
                .map(rutaMapper::toDomain);
    }
}
