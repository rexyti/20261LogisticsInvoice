package com.logistica.domain.strategies;

import com.logistica.liquidacion.domain.enums.LiquidacionEstadoPaquete;
import com.logistica.liquidacion.domain.enums.TipoContratacion;
import com.logistica.liquidacion.domain.models.LiquidacionContrato;
import com.logistica.liquidacion.domain.models.LiquidacionPaquete;
import com.logistica.liquidacion.domain.models.LiquidacionRuta;
import com.logistica.liquidacion.domain.strategies.PorParadaStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PorParadaStrategyTest {

    private PorParadaStrategy strategy;
    private LiquidacionContrato contrato;

    @BeforeEach
    void setUp() {
        strategy = new PorParadaStrategy();
        contrato = LiquidacionContrato.builder()
                .id(UUID.randomUUID())
                .tipoContratacion(TipoContratacion.POR_PARADA)
                .tarifa(new BigDecimal("10.00"))
                .build();
    }

    @Test
    void testCalcularConParadasExitosas() {
        LiquidacionRuta ruta = LiquidacionRuta.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(Arrays.asList(
                        LiquidacionPaquete.builder().id(UUID.randomUUID()).estadoFinal(LiquidacionEstadoPaquete.ENTREGADO).build(),
                        LiquidacionPaquete.builder().id(UUID.randomUUID()).estadoFinal(LiquidacionEstadoPaquete.ENTREGADO).build()
                ))
                .build();
        
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(0, new BigDecimal("20.00").compareTo(resultado), "Debe sumar el 100% de la tarifa por 2 paradas exitosas.");
    }

    @Test
    void testCalcularConParadasFallidasPorCliente() {
        LiquidacionRuta ruta = LiquidacionRuta.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(Arrays.asList(
                        LiquidacionPaquete.builder().id(UUID.randomUUID()).estadoFinal(LiquidacionEstadoPaquete.FALLIDO_CLIENTE).build(),
                        LiquidacionPaquete.builder().id(UUID.randomUUID()).estadoFinal(LiquidacionEstadoPaquete.FALLIDO_CLIENTE).build()
                ))
                .build();
        
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        // 10 * 0.5 + 10 * 0.5 = 10.00
        assertEquals(0, new BigDecimal("10.00").compareTo(resultado), "Debe sumar el 50% de la tarifa por 2 paradas fallidas por cliente.");
    }

    @Test
    void testCalcularConParadasFallidasPorTransportista() {
        LiquidacionRuta ruta = LiquidacionRuta.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(Arrays.asList(
                        LiquidacionPaquete.builder().id(UUID.randomUUID()).estadoFinal(LiquidacionEstadoPaquete.FALLIDO_TRANSPORTISTA).build()
                ))
                .build();
        
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(0, BigDecimal.ZERO.compareTo(resultado), "Debe ser 0 para paradas fallidas por el transportista.");
    }

    @Test
    void testCalcularConMezclaDeEstados() {
        LiquidacionRuta ruta = LiquidacionRuta.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(Arrays.asList(
                        LiquidacionPaquete.builder().id(UUID.randomUUID()).estadoFinal(LiquidacionEstadoPaquete.ENTREGADO).build(), // 10
                        LiquidacionPaquete.builder().id(UUID.randomUUID()).estadoFinal(LiquidacionEstadoPaquete.FALLIDO_CLIENTE).build() // 5
                ))
                .build();
        
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(0, new BigDecimal("15.00").compareTo(resultado));
    }

    @Test
    void testCalcularConRutaSinPaquetesLanzaExcepcion() {
        LiquidacionRuta ruta = LiquidacionRuta.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(java.util.Collections.emptyList())
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> strategy.calcular(ruta, contrato));
    }

    @Test
    void testCalcularConContratoInvalidoLanzaExcepcion() {
        LiquidacionContrato contratoInvalido = LiquidacionContrato.builder()
                .id(UUID.randomUUID())
                .tipoContratacion(TipoContratacion.RECORRIDO_COMPLETO)
                .tarifa(new BigDecimal("100.00"))
                .build();

        LiquidacionRuta ruta = LiquidacionRuta.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(Arrays.asList(LiquidacionPaquete.builder().id(UUID.randomUUID()).estadoFinal(LiquidacionEstadoPaquete.ENTREGADO).build()))
                .build();

        assertThrows(IllegalArgumentException.class, () -> strategy.calcular(ruta, contratoInvalido));
    }
}
