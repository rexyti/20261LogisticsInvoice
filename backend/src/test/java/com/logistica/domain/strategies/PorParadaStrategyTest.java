package com.logistica.domain.strategies;

import com.logistica.domain.models.Contrato;
import com.logistica.domain.models.Paquete;
import com.logistica.domain.models.Ruta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PorParadaStrategyTest {

    private PorParadaStrategy strategy;
    private Contrato contrato;

    @BeforeEach
    void setUp() {
        strategy = new PorParadaStrategy();
        contrato = new Contrato(UUID.randomUUID(), "POR_PARADA", new BigDecimal("10.00")); // Tarifa de $10 por parada
    }

    @Test
    void testCalcularConParadasExitosas() {
        Ruta ruta = new Ruta(UUID.randomUUID(), null, null, Arrays.asList(
                new Paquete(UUID.randomUUID(), "ENTREGADO", ""),
                new Paquete(UUID.randomUUID(), "ENTREGADO", "")
        ));
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(new BigDecimal("20.00"), resultado, "Debe sumar el 100% de la tarifa por 2 paradas exitosas.");
    }

    @Test
    void testCalcularConParadasFallidasPorCliente() {
        Ruta ruta = new Ruta(UUID.randomUUID(), null, null, Arrays.asList(
                new Paquete(UUID.randomUUID(), "FALLIDO_CLIENTE", ""),
                new Paquete(UUID.randomUUID(), "FALLIDO_CLIENTE", "")
        ));
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(new BigDecimal("10.00"), resultado, "Debe sumar el 50% de la tarifa por 2 paradas fallidas por cliente.");
    }

    @Test
    void testCalcularConParadasFallidasPorTransportista() {
        Ruta ruta = new Ruta(UUID.randomUUID(), null, null, Arrays.asList(
                new Paquete(UUID.randomUUID(), "FALLIDO_TRANSPORTISTA", "")
        ));
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(BigDecimal.ZERO, resultado, "Debe ser 0 para paradas fallidas por el transportista.");
    }

    @Test
    void testCalcularConMezclaDeEstados() {
        Ruta ruta = new Ruta(UUID.randomUUID(), null, null, Arrays.asList(
                new Paquete(UUID.randomUUID(), "ENTREGADO", ""), // +10.00
                new Paquete(UUID.randomUUID(), "ENTREGADO", ""), // +10.00
                new Paquete(UUID.randomUUID(), "FALLIDO_CLIENTE", ""), // +5.00
                new Paquete(UUID.randomUUID(), "FALLIDO_TRANSPORTISTA", "") // +0.00
        ));
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(new BigDecimal("25.00"), resultado, "Debe calcular la suma correcta para una mezcla de estados.");
    }

    @Test
    void testCalcularConRutaVacia() {
        Ruta ruta = new Ruta(UUID.randomUUID(), null, null, Collections.emptyList());
        BigDecimal resultado = strategy.calcular(ruta, contrato);
        assertEquals(BigDecimal.ZERO, resultado, "El resultado debe ser 0 si no hay paquetes en la ruta.");
    }
}
