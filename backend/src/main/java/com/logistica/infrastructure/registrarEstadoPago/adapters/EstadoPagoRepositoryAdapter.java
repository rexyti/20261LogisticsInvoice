package com.logistica.infrastructure.registrarEstadoPago.adapters;

import com.logistica.domain.registrarEstadoPago.models.RegistrarEstadoPagoEstadoPago;
import com.logistica.domain.registrarEstadoPago.repositories.RegistrarEstadoPagoEstadoPagoRepository;
import com.logistica.infrastructure.registrarEstadoPago.persistence.repositories.RegistrarEstadoPagoEstadoPagoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoPagoRepositoryAdapter implements RegistrarEstadoPagoEstadoPagoRepository {

    private final RegistrarEstadoPagoEstadoPagoJpaRepository estadoPagoJpaRepository;
    private final PagoMapper pagoMapper;

    @Override
    public RegistrarEstadoPagoEstadoPago save(RegistrarEstadoPagoEstadoPago estadoPago) {
        return pagoMapper.toDomain(estadoPagoJpaRepository.save(pagoMapper.toEntity(estadoPago)));
    }

    @Override
    public Optional<RegistrarEstadoPagoEstadoPago> findUltimoByIdPago(UUID idPago) {
        return estadoPagoJpaRepository
                .findFirstByIdPagoOrderByFechaRegistroDescSecuenciaEventoDesc(idPago)
                .map(pagoMapper::toDomain);
    }

    @Override
    public List<RegistrarEstadoPagoEstadoPago> findAllByIdPago(UUID idPago) {
        return estadoPagoJpaRepository.findByIdPagoOrderByFechaRegistroDesc(idPago)
                .stream().map(pagoMapper::toDomain).toList();
    }
}
