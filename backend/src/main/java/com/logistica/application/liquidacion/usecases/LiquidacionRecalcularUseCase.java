package com.logistica.application.liquidacion.usecases;

import com.logistica.domain.liquidacion.enums.TipoResponsable;
import com.logistica.domain.liquidacion.exceptions.NotFoundException;
import com.logistica.domain.liquidacion.models.Ajuste;
import com.logistica.domain.liquidacion.models.AuditoriaLiquidacion;
import com.logistica.domain.liquidacion.models.Liquidacion;
import com.logistica.domain.liquidacion.repositories.AjusteRepository;
import com.logistica.domain.liquidacion.repositories.AuditoriaLiquidacionRepository;
import com.logistica.domain.liquidacion.repositories.LiquidacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiquidacionRecalcularUseCase {

    private final LiquidacionRepository liquidacionRepository;
    private final AjusteRepository ajusteRepository;
    private final AuditoriaLiquidacionRepository auditoriaRepository;

    @Transactional
    public Liquidacion execute(UUID liquidacionId, List<Ajuste> nuevosAjustes, String responsableId) {

        Liquidacion liquidacion = liquidacionRepository.findById(liquidacionId)
                .orElseThrow(() -> new NotFoundException(liquidacionId));

        if (!liquidacion.isSolicitudRevisionAceptada()) {
            throw new IllegalStateException("No se puede recalcular sin solicitud aprobada");
        }

        BigDecimal valorAnterior = liquidacion.getValorFinal();

        List<Ajuste> ajustesAsociados = nuevosAjustes.stream()
                .map(a -> a.asociarALiquidacion(liquidacionId))
                .toList();

        liquidacion.recalcular(liquidacion.getValorBase(), ajustesAsociados);

        ajusteRepository.saveAll(ajustesAsociados);
        Liquidacion liquidacionActualizada = liquidacionRepository.save(liquidacion);

        AuditoriaLiquidacion auditoria = AuditoriaLiquidacion.crearRecalculo(
                liquidacionId,
                valorAnterior,
                liquidacionActualizada.getValorFinal(),
                TipoResponsable.ADMINISTRADOR,
                responsableId
        );

        auditoriaRepository.save(auditoria);

        return liquidacionActualizada;
    }
}
