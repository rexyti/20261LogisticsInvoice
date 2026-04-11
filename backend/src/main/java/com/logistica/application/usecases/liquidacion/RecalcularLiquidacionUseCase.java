package com.logistica.application.usecases.liquidacion;

import com.logistica.domain.models.Ajuste;
import com.logistica.domain.models.AuditoriaLiquidacion;
import com.logistica.domain.models.Liquidacion;
import com.logistica.domain.repositories.AjusteRepository;
import com.logistica.domain.repositories.AuditoriaLiquidacionRepository;
import com.logistica.domain.repositories.LiquidacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RecalcularLiquidacionUseCase {

    private final LiquidacionRepository liquidacionRepository;
    private final AjusteRepository ajusteRepository;
    private final AuditoriaLiquidacionRepository auditoriaRepository;

    public RecalcularLiquidacionUseCase(LiquidacionRepository liquidacionRepository, AjusteRepository ajusteRepository, AuditoriaLiquidacionRepository auditoriaRepository) {
        this.liquidacionRepository = liquidacionRepository;
        this.ajusteRepository = ajusteRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public Liquidacion execute(UUID liquidacionId, List<Ajuste> nuevosAjustes, String responsable) {
        Liquidacion liquidacion = liquidacionRepository.findById(liquidacionId)
                .orElseThrow(() -> new com.logistica.domain.exceptions.LiquidacionNotFoundException("No se encontró la liquidación con ID: " + liquidacionId));

        BigDecimal valorAnterior = liquidacion.getValorFinal();

        // Lógica para aplicar los nuevos ajustes.
        // Esto podría ser simplemente sumarlos al valor existente,
        // o podría implicar una lógica más compleja.
        BigDecimal totalNuevosAjustes = nuevosAjustes.stream()
                .map(Ajuste::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorNuevo = liquidacion.getValorFinal().add(totalNuevosAjustes);
        liquidacion.setValorFinal(valorNuevo);
        liquidacion.setEstado("RECALCULADA");

        ajusteRepository.saveAll(nuevosAjustes);
        Liquidacion liquidacionActualizada = liquidacionRepository.save(liquidacion);

        AuditoriaLiquidacion auditoria = new AuditoriaLiquidacion(
                UUID.randomUUID(),
                liquidacionId,
                "RECALCULO",
                valorAnterior,
                valorNuevo,
                OffsetDateTime.now(),
                responsable
        );
        auditoriaRepository.save(auditoria);

        return liquidacionActualizada;
    }
}
