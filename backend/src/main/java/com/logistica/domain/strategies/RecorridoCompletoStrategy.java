package com.logistica.domain.strategies;

import com.logistica.domain.models.Contrato;
import com.logistica.domain.models.Ruta;

import java.math.BigDecimal;

public class RecorridoCompletoStrategy implements LiquidacionStrategy {

    @Override
    public BigDecimal calcular(Ruta ruta, Contrato contrato) {

        validarContrato(contrato);
        validarRuta(ruta);

        if (!ruta.fueCompletada()) {
            return BigDecimal.ZERO;
        }

        return contrato.getTarifaSegura();
    }

    private void validarRuta(Ruta ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser null");
        }

        if (!ruta.tienePaquetes()) {
            throw new IllegalArgumentException("La ruta no tiene paquetes para calcular");
        }
    }
    private void validarContrato(Contrato contrato) {
        if ( contrato == null || !contrato.esRecorridoCompleto()) {
            throw new IllegalArgumentException("El contrato no es de tipo RECORRIDO_COMPLETO");
        }
    }
}
