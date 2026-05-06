package com.logistica.application.liquidacion.usecases;

import com.logistica.domain.liquidacion.exceptions.ContratoTarifaNoEncontradaException;
import com.logistica.domain.liquidacion.exceptions.DuplicadaException;
import com.logistica.domain.liquidacion.models.AuditoriaLiquidacion;
import com.logistica.domain.liquidacion.models.ContratoTarifa;
import com.logistica.domain.liquidacion.models.Liquidacion;
import com.logistica.domain.liquidacion.models.RutaLiquidacion;
import com.logistica.domain.liquidacion.repositories.AuditoriaLiquidacionRepository;
import com.logistica.domain.liquidacion.repositories.ContratoTarifaRepository;
import com.logistica.domain.liquidacion.repositories.LiquidacionRepository;
import com.logistica.domain.liquidacion.strategies.Strategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiquidacionCalcularUseCase {

    private final LiquidacionRepository liquidacionRepository;
    private final ContratoTarifaRepository contratoRepository;
    private final AuditoriaLiquidacionRepository auditoriaRepository;
    private final StrategyFactory strategyFactory;

    @Transactional
    public Liquidacion execute(RutaLiquidacion ruta, UUID idContrato) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }

        if (!ruta.tienePaquetes()) {
            throw new IllegalArgumentException("La ruta debe tener al menos un paquete");
        }

        ContratoTarifa contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new ContratoTarifaNoEncontradaException(idContrato));

        if (liquidacionRepository.existsByIdRuta(ruta.getId())) {
            throw new DuplicadaException(ruta.getId());
        }

        Strategy strategy = strategyFactory.getStrategy(contrato.getTipoContratacion());
        BigDecimal valorBase = strategy.calcular(ruta, contrato);

        Liquidacion liquidacion = Liquidacion.crear(ruta.getId(), contrato.getId(), valorBase);
        Liquidacion savedLiquidacion = liquidacionRepository.save(liquidacion);

        AuditoriaLiquidacion auditoriaCalculo = AuditoriaLiquidacion.crearCalculo(
                savedLiquidacion.getId(),
                savedLiquidacion.getValorFinal()
        );

        auditoriaRepository.save(auditoriaCalculo);

        return savedLiquidacion;
    }
}
