package com.logistica.liquidacion.application.usecases;

import com.logistica.liquidacion.domain.exceptions.LiquidacionContratoNotFoundException;
import com.logistica.liquidacion.domain.exceptions.LiquidacionDuplicadaException;
import com.logistica.liquidacion.domain.models.AuditoriaLiquidacion;
import com.logistica.liquidacion.domain.models.LiquidacionContrato;
import com.logistica.liquidacion.domain.models.Liquidacion;
import com.logistica.liquidacion.domain.models.LiquidacionRuta;
import com.logistica.liquidacion.domain.repositories.AuditoriaLiquidacionRepository;
import com.logistica.liquidacion.domain.repositories.LiquidacionContratoRepository;
import com.logistica.liquidacion.domain.repositories.LiquidacionRepository;
import com.logistica.liquidacion.domain.strategies.LiquidacionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiquidacionCalcularUseCase {

    private final LiquidacionRepository liquidacionRepository;
    private final LiquidacionContratoRepository  contratoRepository;
    private final AuditoriaLiquidacionRepository auditoriaRepository;
    private final LiquidacionStrategyFactory strategyFactory;

    @Transactional
    public Liquidacion execute(LiquidacionRuta ruta, UUID idContrato) {
        // 1. Validaciones de entrada
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }

        if (!ruta.tienePaquetes()) {
            throw new IllegalArgumentException("La ruta debe tener al menos un paquete");
        }

        // 2. Obtener el contrato desde la base de datos
        LiquidacionContrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new LiquidacionContratoNotFoundException(idContrato));

        // 3. Validar duplicado
        if (liquidacionRepository.existsByIdRuta(ruta.getId())) {
            throw new LiquidacionDuplicadaException(ruta.getId());
        }

        // 4. Calcular valor base
        LiquidacionStrategy strategy = strategyFactory.getStrategy(contrato.getTipoContratacion());
        BigDecimal valorBase = strategy.calcular(ruta, contrato);

        // 5. Crear liquidación
        Liquidacion liquidacion = Liquidacion.crear(ruta.getId(), contrato.getId(), valorBase);
        Liquidacion savedLiquidacion = liquidacionRepository.save(liquidacion);

        // 6. Registrar auditoría
        AuditoriaLiquidacion auditoriaCalculo = AuditoriaLiquidacion.crearCalculo(
                savedLiquidacion.getId(),
                savedLiquidacion.getValorFinal()
        );

        auditoriaRepository.save(auditoriaCalculo);

        return savedLiquidacion;
    }
}
