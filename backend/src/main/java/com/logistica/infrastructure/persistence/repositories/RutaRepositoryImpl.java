package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.RutaRepository;
import com.logistica.infrastructure.adapters.RutaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public List<Ruta> listarTodas() {
        return jpaRepository.findAll().stream()
                .map(rutaMapper::toDomain)
                .toList();
    }
}
