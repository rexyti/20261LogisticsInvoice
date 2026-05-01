package com.logistica.RegistrarEstadoPago.infrastructure.adapters;

import com.logistica.RegistrarEstadoPago.domain.models.LiquidacionReferencia;
import com.logistica.RegistrarEstadoPago.domain.repositories.RegistrarEstadoPagoLiquidacionRepository;
import com.logistica.RegistrarEstadoPago.infrastructure.persistence.repositories.RegistrarEstadoPagoLiquidacionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LiquidacionRepositoryAdapter implements RegistrarEstadoPagoLiquidacionRepository {

    private final RegistrarEstadoPagoLiquidacionJpaRepository liquidacionJpaRepository;
    private final PagoMapper pagoMapper;

    @Override
    public Optional<LiquidacionReferencia> findById(UUID idLiquidacion) {
        return liquidacionJpaRepository.findById(idLiquidacion).map(pagoMapper::toDomain);
    }

    @Override
    public LiquidacionReferencia save(LiquidacionReferencia liquidacion) {
        return pagoMapper.toDomain(liquidacionJpaRepository.save(pagoMapper.toEntity(liquidacion)));
    }
}
