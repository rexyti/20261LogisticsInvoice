package com.logistica.infrastructure.visualizarEstadoPago.persistence.repositories;

import com.logistica.domain.visualizarEstadoPago.models.VisualizarEstadoPagoPago;
import com.logistica.domain.visualizarEstadoPago.repositories.VisualizarEstadoPagoPagoRepository;
import com.logistica.infrastructure.visualizarEstadoPago.adapters.PagoPersistenceMapper;
import com.logistica.infrastructure.visualizarEstadoPago.persistence.entities.VisualizarEstadoPagoPagoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PagoRepositoryImpl implements VisualizarEstadoPagoPagoRepository {

    @Autowired
    private VisualizarEstadoPagoPagoJpaRepository jpaRepository;

    @Autowired
    private PagoPersistenceMapper pagoMapper;

    @Override
    public Optional<VisualizarEstadoPagoPago> findById(UUID id) {
        return jpaRepository.findById(id).map(pagoMapper::toDomain);
    }

    @Override
    public Optional<VisualizarEstadoPagoPago> findByIdAndUsuarioId(UUID id, UUID usuarioId) {
        return jpaRepository.findByIdPagoAndUsuarioId(id, usuarioId).map(pagoMapper::toDomain);
    }

    @Override
    public List<VisualizarEstadoPagoPago> findByUsuarioId(UUID usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream()
                .map(pagoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public VisualizarEstadoPagoPago save(VisualizarEstadoPagoPago pago) {
        VisualizarEstadoPagoPagoEntity entity = pagoMapper.toEntity(pago);
        VisualizarEstadoPagoPagoEntity savedEntity = jpaRepository.save(entity);
        return pagoMapper.toDomain(savedEntity);
    }
}
