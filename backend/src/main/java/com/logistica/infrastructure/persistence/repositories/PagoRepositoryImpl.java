package com.logistica.infrastructure.persistence.repositories;

import com.logistica.domain.models.Pago;
import com.logistica.domain.repositories.PagoRepository;
import com.logistica.infrastructure.adapters.PagoPersistenceMapper;
import com.logistica.infrastructure.persistence.entities.PagoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PagoRepositoryImpl implements PagoRepository {

    @Autowired
    private PagoJpaRepository jpaRepository;

    @Autowired
    private PagoPersistenceMapper pagoMapper;

    @Override
    public Optional<Pago> findById(UUID id) {
        return jpaRepository.findById(id).map(pagoMapper::toDomain);
    }

    @Override
    public Optional<Pago> findByIdAndUsuarioId(UUID id, UUID usuarioId) {
        return jpaRepository.findByIdAndUsuarioId(id, usuarioId).map(pagoMapper::toDomain);
    }

    @Override
    public List<Pago> findByUsuarioId(UUID usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream()
                .map(pagoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Pago save(Pago pago) {
        PagoEntity entity = pagoMapper.toEntity(pago);
        PagoEntity savedEntity = jpaRepository.save(entity);
        return pagoMapper.toDomain(savedEntity);
    }
}
