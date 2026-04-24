package com.logistica.liquidacion.application.usecases;

import com.logistica.liquidacion.domain.enums.TipoResponsable;
import com.logistica.liquidacion.domain.exceptions.LiquidacionNotFoundException;
import com.logistica.liquidacion.domain.models.Ajuste;
import com.logistica.liquidacion.domain.models.AuditoriaLiquidacion;
import com.logistica.liquidacion.domain.models.Liquidacion;
import com.logistica.liquidacion.domain.repositories.AjusteRepository;
import com.logistica.liquidacion.domain.repositories.AuditoriaLiquidacionRepository;
import com.logistica.liquidacion.domain.repositories.LiquidacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecalcularLiquidacionUseCase {

    private final LiquidacionRepository liquidacionRepository;
    private final AjusteRepository ajusteRepository;
    private final AuditoriaLiquidacionRepository auditoriaRepository;

    @Transactional
    public Liquidacion execute(UUID liquidacionId, List<Ajuste> nuevosAjustes, String responsableId) {

        // 1. Buscar liquidación
        Liquidacion liquidacion = liquidacionRepository.findById(liquidacionId)
                .orElseThrow(() -> new LiquidacionNotFoundException(liquidacionId));

        if (!liquidacion.isSolicitudRevisionAceptada()) {
            throw new IllegalStateException("No se puede recalcular sin solicitud aprobada");
        }

        BigDecimal valorAnterior = liquidacion.getValorFinal();

        // 2. Asociar ajustes
        List<Ajuste> ajustesAsociados = nuevosAjustes.stream()
                .map(a -> a.asociarALiquidacion(liquidacionId))
                .toList();

        // 3. Recalcular (dominio)
        liquidacion.recalcular(liquidacion.getValorBase(), ajustesAsociados);

        // 4. Persistir
        ajusteRepository.saveAll(ajustesAsociados);
        Liquidacion liquidacionActualizada = liquidacionRepository.save(liquidacion);

        // 5. Auditoría
        AuditoriaLiquidacion auditoria = AuditoriaLiquidacion.crearRecalculo(
                liquidacionId,
                valorAnterior,
                liquidacionActualizada.getValorFinal(),
                TipoResponsable.ADMINISTRADOR, // Asumimos que quien recalcula es un admin
                responsableId
        );

        auditoriaRepository.save(auditoria);

        return liquidacionActualizada;
    }
}
