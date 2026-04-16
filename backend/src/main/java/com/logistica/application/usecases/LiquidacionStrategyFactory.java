package com.logistica.application.usecases;

import com.logistica.domain.enums.TipoContratacion;
import com.logistica.domain.strategies.LiquidacionStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LiquidacionStrategyFactory {

    private final Map<TipoContratacion, LiquidacionStrategy> strategies;

    public LiquidacionStrategyFactory(List<LiquidacionStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        LiquidacionStrategy::soporta,
                        strategy -> strategy
                ));
    }

    public LiquidacionStrategy getStrategy(TipoContratacion tipo) {
        LiquidacionStrategy strategy = strategies.get(tipo);

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No existe estrategia para el tipo: " + tipo
            );
        }

        return strategy;
    }
}