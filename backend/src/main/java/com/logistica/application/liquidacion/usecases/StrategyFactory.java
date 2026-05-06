package com.logistica.application.liquidacion.usecases;

import com.logistica.domain.liquidacion.enums.TipoContratacion;
import com.logistica.domain.liquidacion.strategies.Strategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StrategyFactory {

    private final Map<TipoContratacion, Strategy> strategies;

    public StrategyFactory(List<Strategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        Strategy::soporta,
                        strategy -> strategy
                ));
    }

    public Strategy getStrategy(TipoContratacion tipo) {
        Strategy strategy = strategies.get(tipo);

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No existe estrategia para el tipo: " + tipo
            );
        }

        return strategy;
    }
}