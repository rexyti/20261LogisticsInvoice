package com.logistica.domain.strategies;

import com.logistica.domain.models.Contrato;
import com.logistica.domain.models.Ruta;

import java.math.BigDecimal;

public class RecorridoCompletoStrategy implements LiquidacionStrategy {

    @Override
    public BigDecimal calcular(Ruta ruta, Contrato contrato) {
        // Asume que si la ruta se considera "completa", se paga la tarifa completa.
        // La lógica para determinar si una ruta está "completa" puede ser más compleja
        // y podría necesitar más datos del modelo Ruta.
        boolean rutaCompleta = ruta.getPaquetes().stream()
                .allMatch(p -> "ENTREGADO".equals(p.getEstadoFinal()));

        if (rutaCompleta) {
            return contrato.getTarifa();
        } else {
            return BigDecimal.ZERO;
        }
    }
}
