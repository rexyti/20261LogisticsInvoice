package com.logistica.application.usecases.liquidacion;

import com.logistica.domain.exceptions.ContratoNotFoundException;
import com.logistica.domain.exceptions.LiquidacionDuplicadaException;
import com.logistica.domain.models.AuditoriaLiquidacion;
import com.logistica.domain.models.Contrato;
import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.models.Ruta;
import com.logistica.domain.repositories.AuditoriaLiquidacionRepository;
import com.logistica.domain.repositories.ContratoRepository;
import com.logistica.domain.repositories.LiquidacionRepository;
import com.logistica.domain.strategies.LiquidacionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalcularLiquidacionUseCase {

    private final LiquidacionRepository liquidacionRepository;
    private final ContratoRepository contratoRepository;
    private final AuditoriaLiquidacionRepository auditoriaRepository;
    private final LiquidacionStrategyFactory strategyFactory;

    @Transactional
    public Liquidacion execute(Ruta ruta, UUID idContrato) {
        // 1. Validar que no exista una liquidación duplicada
        if (liquidacionRepository.existsByIdRuta(ruta.getId())) {
            throw new LiquidacionDuplicadaException("Ya existe una liquidación para la ruta con ID: " + ruta.getId());
        }

        // 2. Obtener el contrato desde la base de datos
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new ContratoNotFoundException("No se encontró el contrato con ID: " + idContrato));

        // 3. Seleccionar la estrategia y calcular el valor base
        LiquidacionStrategy strategy = strategyFactory.getStrategy(contrato.getTipoContratacion());
        BigDecimal valorBase = strategy.calcular(ruta, contrato);

        // 4. Crear la liquidación usando la regla de negocio del modelo
        Liquidacion liquidacion = Liquidacion.crear(ruta.getId(), contrato.getId(), valorBase);
        Liquidacion savedLiquidacion = liquidacionRepository.save(liquidacion);

        // 5. Registrar auditoría inicial
        AuditoriaLiquidacion auditoria = AuditoriaLiquidacion.crearCalculo(savedLiquidacion.getId(), valorBase);
        auditoriaRepository.save(auditoria);

        return savedLiquidacion;
    }
}
