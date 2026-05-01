package com.logistica.liquidacion.domain.strategies;

import com.logistica.liquidacion.domain.enums.TipoContratacion;
import com.logistica.liquidacion.domain.models.LiquidacionContrato;
import com.logistica.liquidacion.domain.models.LiquidacionRuta;

import java.math.BigDecimal;

public interface LiquidacionStrategy {

    TipoContratacion soporta();

    BigDecimal calcular(LiquidacionRuta ruta, LiquidacionContrato contrato);
}