package com.logistica.liquidacion.application.usecases;

import com.logistica.liquidacion.domain.exceptions.ContratoNotFoundException;
import com.logistica.liquidacion.domain.exceptions.LiquidacionDuplicadaException;
import com.logistica.liquidacion.domain.models.AuditoriaLiquidacion;
import com.logistica.liquidacion.domain.models.Contrato;
import com.logistica.liquidacion.domain.models.Liquidacion;
import com.logistica.liquidacion.domain.models.Ruta;
import com.logistica.liquidacion.domain.repositories.AuditoriaLiquidacionRepository;
import com.logistica.liquidacion.domain.repositories.ContratoRepository;
import com.logistica.liquidacion.domain.repositories.LiquidacionRepository;
import com.logistica.liquidacion.domain.strategies.LiquidacionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalcularLiquidacionUseCase {

    private final LiquidacionRepository liquidacionRepository;
    private final ContratoRepository  contratoRepository;
    private final AuditoriaLiquidacionRepository auditoriaRepository;
    private final LiquidacionStrategyFactory strategyFactory;

    @Transactional
    public Liquidacion execute(Ruta ruta, UUID idContrato) {
        // 1. Validaciones de entrada
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }

        if (!ruta.tienePaquetes()) {
            throw new IllegalArgumentException("La ruta debe tener al menos un paquete");
        }

        // 2. Obtener el contrato desde la base de datos
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new ContratoNotFoundException(idContrato));

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
