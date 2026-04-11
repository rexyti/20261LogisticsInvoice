package com.logistica.domain.strategies;

import com.logistica.domain.models.Contrato;
import com.logistica.domain.models.Paquete;
import com.logistica.domain.models.Ruta;

import java.math.BigDecimal;

public class PorParadaStrategy implements LiquidacionStrategy {

    @Override
    public BigDecimal calcular(Ruta ruta, Contrato contrato) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal tarifaPorParada = contrato.getTarifa();

        for (Paquete paquete : ruta.getPaquetes()) {
            BigDecimal valorParada = switch (paquete.getEstadoFinal()) {
                case "ENTREGADO" -> tarifaPorParada;
                case "FALLIDO_CLIENTE" -> tarifaPorParada.multiply(new BigDecimal("0.5")); // 50%
                case "FALLIDO_TRANSPORTISTA" -> BigDecimal.ZERO;
                default -> BigDecimal.ZERO;
            };
            total = total.add(valorParada);
        }
        return total;
    }
}
// a futuro el rol de administrador debe configurar el porcentaje de case FALLIDO CLIENTE