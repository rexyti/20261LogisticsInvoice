package com.logistica.domain.strategies;

import com.logistica.domain.models.Contrato;
import com.logistica.domain.models.Paquete;
import com.logistica.domain.models.Ruta;

import java.math.BigDecimal;

public class PorParadaStrategy implements LiquidacionStrategy {

    private static final BigDecimal PORCENTAJE_FALLIDO_CLIENTE = new BigDecimal("0.5");

    @Override
    public BigDecimal calcular(Ruta ruta, Contrato contrato) {

        validarContrato(contrato);
        validarRuta(ruta);

        BigDecimal tarifa = contrato.getTarifaSegura();

        return ruta.getPaquetes().stream()
                .map(p -> calcularValorPorPaquete(p, tarifa))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularValorPorPaquete(Paquete paquete, BigDecimal tarifa) {

        BigDecimal factor = paquete.obtenerFactorPago(PORCENTAJE_FALLIDO_CLIENTE);

        return tarifa.multiply(factor);
    }

    private void validarContrato(Contrato contrato) {
        if (contrato == null || !contrato.esPorParada()) {
            throw new IllegalArgumentException("El contrato no es de tipo POR_PARADA");
        }
    }

    private void validarRuta(Ruta ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser null");
        }

        if (!ruta.tienePaquetes()) {
            throw new IllegalArgumentException("La ruta no tiene paquetes para calcular");
        }
    }
}