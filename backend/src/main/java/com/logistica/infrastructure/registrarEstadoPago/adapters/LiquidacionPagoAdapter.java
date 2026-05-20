package com.logistica.infrastructure.registrarEstadoPago.adapters;

import com.logistica.domain.liquidacion.enums.EstadoLiquidacion;
import com.logistica.domain.registrarEstadoPago.ports.LiquidacionPagoPort;
import com.logistica.infrastructure.liquidacion.persistence.repositories.LiquidacionJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiquidacionPagoAdapter implements LiquidacionPagoPort {

    private final LiquidacionJpaRepository liquidacionJpaRepository;

    @Override
    public boolean existe(UUID idLiquidacion) {
        return liquidacionJpaRepository.existsById(idLiquidacion);
    }

    @Override
    public void marcarComoPagada(UUID idLiquidacion) {
        liquidacionJpaRepository.findById(idLiquidacion).ifPresentOrElse(
                entity -> {
                    entity.setEstado(EstadoLiquidacion.PAGADA);
                    liquidacionJpaRepository.save(entity);
                    log.info("Liquidación {} marcada como PAGADA", idLiquidacion);
                },
                () -> log.warn("Liquidación {} no encontrada al intentar marcar como PAGADA", idLiquidacion)
        );
    }
}
