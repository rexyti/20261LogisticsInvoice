package com.logistica.RegistrarEstadoPago.infrastructure.adapters;

import com.logistica.RegistrarEstadoPago.domain.models.EstadoPago;
import com.logistica.RegistrarEstadoPago.domain.repositories.EstadoPagoRepository;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories.EstadoPagoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoPagoRepositoryAdapter implements EstadoPagoRepository {

    private final EstadoPagoJpaRepository estadoPagoJpaRepository;
    private final PagoMapper pagoMapper;

    @Override
    public EstadoPago save(EstadoPago estadoPago) {
        return pagoMapper.toDomain(estadoPagoJpaRepository.save(pagoMapper.toEntity(estadoPago)));
    }

    @Override
    public Optional<EstadoPago> findUltimoByIdPago(UUID idPago) {
        return estadoPagoJpaRepository
                .findFirstByIdPagoOrderByFechaRegistroDescSecuenciaEventoDesc(idPago)
                .map(pagoMapper::toDomain);
    }

    @Override
    public List<EstadoPago> findAllByIdPago(UUID idPago) {
        return estadoPagoJpaRepository.findByIdPagoOrderByFechaRegistroDesc(idPago)
                .stream().map(pagoMapper::toDomain).toList();
    }
}
