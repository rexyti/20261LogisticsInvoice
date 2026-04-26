package com.logistica.infrastructure.adapters;

import com.logistica.domain.models.LiquidacionReferencia;
import com.logistica.domain.repositories.LiquidacionRepository;
import com.logistica.infrastructure.persistence.repositories.LiquidacionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LiquidacionRepositoryAdapter implements LiquidacionRepository {

    private final LiquidacionJpaRepository liquidacionJpaRepository;
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
