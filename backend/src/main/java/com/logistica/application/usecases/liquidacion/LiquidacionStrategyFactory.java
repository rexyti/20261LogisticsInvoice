package com.logistica.application.usecases.liquidacion;

import com.logistica.domain.strategies.LiquidacionStrategy;
import com.logistica.domain.strategies.PorParadaStrategy;
import com.logistica.domain.strategies.RecorridoCompletoStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LiquidacionStrategyFactory {

    private final Map<String, LiquidacionStrategy> strategies;

    public LiquidacionStrategyFactory() {
        strategies = Map.of(
                "POR_PARADA", new PorParadaStrategy(),
                "RECORRIDO_COMPLETO", new RecorridoCompletoStrategy()
        );
    }

    public LiquidacionStrategy getStrategy(String tipoContratacion) {
        LiquidacionStrategy strategy = strategies.get(tipoContratacion);
        if (strategy == null) {
            throw new IllegalArgumentException("Tipo de contratación no válido: " + tipoContratacion);
        }
        return strategy;
    }
}
