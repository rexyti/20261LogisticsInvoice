package com.logistica.liquidacion.domain.strategies;

import com.logistica.liquidacion.domain.enums.TipoContratacion;
import com.logistica.liquidacion.domain.models.Contrato;
import com.logistica.liquidacion.domain.models.Ruta;

import java.math.BigDecimal;

public interface LiquidacionStrategy {

    TipoContratacion soporta();

    BigDecimal calcular(Ruta ruta, Contrato contrato);
}