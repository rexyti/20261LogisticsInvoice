package com.logistica.domain.strategies;

import com.logistica.domain.liquidacion.enums.EstadoPaquete;
import com.logistica.domain.liquidacion.enums.TipoContratacion;
import com.logistica.domain.liquidacion.models.ContratoTarifa;
import com.logistica.domain.liquidacion.models.Paquete;
import com.logistica.domain.liquidacion.models.RutaLiquidacion;
import com.logistica.domain.liquidacion.strategies.PorParadaStrategy;
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
    private ContratoTarifa contrato;

    @BeforeEach
    void setUp() {
        strategy = new PorParadaStrategy();
        contrato = ContratoTarifa.builder()
                .id(UUID.randomUUID())
                .tipoContratacion(TipoContratacion.POR_PARADA)
                .tarifa(new BigDecimal("10.00"))
                .build();
    }

    @Test
    void testCalcularConParadasExitosas() {
        RutaLiquidacion ruta = RutaLiquidacion.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(Arrays.asList(
                        Paquete.builder().id(UUID.randomUUID()).estadoFinal(EstadoPaquete.ENTREGADO).build(),
                        Paquete.builder().id(UUID.randomUUID()).estadoFinal(EstadoPaquete.ENTREGADO).build()
                ))
                .build();
        
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(0, new BigDecimal("20.00").compareTo(resultado), "Debe sumar el 100% de la tarifa por 2 paradas exitosas.");
    }

    @Test
    void testCalcularConParadasFallidasPorCliente() {
        RutaLiquidacion ruta = RutaLiquidacion.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(Arrays.asList(
                        Paquete.builder().id(UUID.randomUUID()).estadoFinal(EstadoPaquete.FALLIDO_CLIENTE).build(),
                        Paquete.builder().id(UUID.randomUUID()).estadoFinal(EstadoPaquete.FALLIDO_CLIENTE).build()
                ))
                .build();
        
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        // 10 * 0.5 + 10 * 0.5 = 10.00
        assertEquals(0, new BigDecimal("10.00").compareTo(resultado), "Debe sumar el 50% de la tarifa por 2 paradas fallidas por cliente.");
    }

    @Test
    void testCalcularConParadasFallidasPorTransportista() {
        RutaLiquidacion ruta = RutaLiquidacion.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(Arrays.asList(
                        Paquete.builder().id(UUID.randomUUID()).estadoFinal(EstadoPaquete.FALLIDO_TRANSPORTISTA).build()
                ))
                .build();
        
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(0, BigDecimal.ZERO.compareTo(resultado), "Debe ser 0 para paradas fallidas por el transportista.");
    }

    @Test
    void testCalcularConMezclaDeEstados() {
        RutaLiquidacion ruta = RutaLiquidacion.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(Arrays.asList(
                        Paquete.builder().id(UUID.randomUUID()).estadoFinal(EstadoPaquete.ENTREGADO).build(), // 10
                        Paquete.builder().id(UUID.randomUUID()).estadoFinal(EstadoPaquete.FALLIDO_CLIENTE).build() // 5
                ))
                .build();
        
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(0, new BigDecimal("15.00").compareTo(resultado));
    }

    @Test
    void testCalcularConRutaSinPaquetesLanzaExcepcion() {
        RutaLiquidacion ruta = RutaLiquidacion.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(java.util.Collections.emptyList())
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> strategy.calcular(ruta, contrato));
    }

    @Test
    void testCalcularConContratoInvalidoLanzaExcepcion() {
        ContratoTarifa contratoInvalido = ContratoTarifa.builder()
                .id(UUID.randomUUID())
                .tipoContratacion(TipoContratacion.RECORRIDO_COMPLETO)
                .tarifa(new BigDecimal("100.00"))
                .build();

        RutaLiquidacion ruta = RutaLiquidacion.builder()
                .id(UUID.randomUUID())
                .fechaInicio(OffsetDateTime.now().minusHours(1))
                .fechaCierre(OffsetDateTime.now())
                .paquetes(Arrays.asList(Paquete.builder().id(UUID.randomUUID()).estadoFinal(EstadoPaquete.ENTREGADO).build()))
                .build();

        assertThrows(IllegalArgumentException.class, () -> strategy.calcular(ruta, contratoInvalido));
    }
}
