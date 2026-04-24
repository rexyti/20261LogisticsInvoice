package com.logistica.liquidacion.domain.strategies;

import com.logistica.liquidacion.domain.enums.TipoContratacion;
import com.logistica.liquidacion.domain.models.Contrato;
import com.logistica.liquidacion.domain.models.Paquete;
import com.logistica.liquidacion.domain.models.Ruta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PorParadaStrategy implements LiquidacionStrategy {

    private static final BigDecimal PORCENTAJE_FALLIDO_CLIENTE = new BigDecimal("0.5");

    @Override
    public TipoContratacion soporta() {
        return TipoContratacion.POR_PARADA;
    }

    @Override
    public BigDecimal calcular(Ruta ruta, Contrato contrato) {

        validarContrato(contrato);
        validarRuta(ruta);

        BigDecimal tarifa = contrato.getTarifaSegura();

        return ruta.obtenerPaquetesValidos().stream()
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
        if (ruta == null || !ruta.tienePaquetes()) {
            throw new IllegalArgumentException("La ruta no tiene paquetes válidos");
        }
    }
}