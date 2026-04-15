package com.logistica.domain.strategies;

import com.logistica.domain.models.Ruta;
import com.logistica.domain.models.Contrato;
import java.math.BigDecimal;

public interface LiquidacionStrategy {

    boolean soporta(Contrato contrato)

    BigDecimal calcular(Ruta ruta, Contrato contrato);
}
