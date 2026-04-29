package com.logistica.cierreRuta.infrastructure.persistence.repositories;

import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.domain.models.Ruta;
import com.logistica.cierreRuta.domain.repositories.RutaRepository;
import com.logistica.cierreRuta.infrastructure.adapters.RutaMapper;
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
    public Ruta guardar(Ruta ruta) {
        var entity = rutaMapper.toEntity(ruta);
        var saved = jpaRepository.save(entity);
        return rutaMapper.toDomain(saved);
    }

    @Override
    public Optional<Ruta> buscarPorRutaId(UUID rutaId) {
        return jpaRepository.findByRutaId(rutaId).map(rutaMapper::toDomain);
    }

    @Override
    public Page<Ruta> listarTodas(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(rutaMapper::toDomain);
    }

    @Override
    public Page<Ruta> buscarPorEstado(EstadoProcesamiento estado, Pageable pageable) {
        return jpaRepository.findByEstadoProcesamiento(estado, pageable)
                .map(rutaMapper::toDomain);
    }
}
