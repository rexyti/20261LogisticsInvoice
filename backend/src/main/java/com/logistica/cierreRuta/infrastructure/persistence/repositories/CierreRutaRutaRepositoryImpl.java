package com.logistica.cierreRuta.infrastructure.persistence.repositories;

import com.logistica.cierreRuta.domain.enums.EstadoProcesamiento;
import com.logistica.cierreRuta.domain.models.CierreRutaRuta;
import com.logistica.cierreRuta.domain.repositories.RutaRepository;
import com.logistica.cierreRuta.infrastructure.adapters.CierreRutaRutaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CierreRutaRutaRepositoryImpl implements RutaRepository {

    private final CierreRutaRutaJpaRepository jpaRepository;
    private final CierreRutaRutaMapper rutaMapper;

    @Override
    public boolean existsByRutaId(UUID rutaId) {
        return jpaRepository.existsByRutaId(rutaId);
    }

    @Override
    public CierreRutaRuta guardar(CierreRutaRuta ruta) {
        var entity = rutaMapper.toEntity(ruta);
        var saved = jpaRepository.save(entity);
        return rutaMapper.toDomain(saved);
    }

    @Override
    public Optional<CierreRutaRuta> buscarPorRutaId(UUID rutaId) {
        return jpaRepository.findByRutaId(rutaId).map(rutaMapper::toDomain);
    }

    @Override
    public Page<CierreRutaRuta> listarTodas(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(rutaMapper::toDomain);
    }

    @Override
    public Page<CierreRutaRuta> buscarPorEstado(EstadoProcesamiento estado, Pageable pageable) {
        return jpaRepository.findByEstadoProcesamiento(estado, pageable)
                .map(rutaMapper::toDomain);
    }
}
