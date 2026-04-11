package com.logistica.application.usecases.liquidacion;

import com.logistica.domain.models.Contrato;
import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.LiquidacionRepository;
import com.logistica.domain.strategies.LiquidacionStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CalcularLiquidacionUseCase {

    private final LiquidacionRepository liquidacionRepository;
    private final LiquidacionStrategyFactory strategyFactory;

    public CalcularLiquidacionUseCase(LiquidacionRepository liquidacionRepository, LiquidacionStrategyFactory strategyFactory) {
        this.liquidacionRepository = liquidacionRepository;
        this.strategyFactory = strategyFactory;
    }

    @Transactional
    public Liquidacion execute(Ruta ruta, Contrato contrato) {
        if (liquidacionRepository.existsByIdRuta(ruta.getId())) {
            throw new com.logistica.domain.exceptions.LiquidacionDuplicadaException("Ya existe una liquidación para la ruta con ID: " + ruta.getId());
        }

        LiquidacionStrategy strategy = strategyFactory.getStrategy(contrato.getTipoContratacion());
        java.math.BigDecimal valorFinal = strategy.calcular(ruta, contrato);

        Liquidacion liquidacion = new Liquidacion(
                UUID.randomUUID(),
                ruta.getId(),
                contrato.getId(),
                "CALCULADA",
                valorFinal,
                OffsetDateTime.now(),
                null // Los ajustes se pueden agregar más tarde
        );

        return liquidacionRepository.save(liquidacion);
    }
}
