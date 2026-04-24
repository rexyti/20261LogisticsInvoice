package com.logistica.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.logistica.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.application.usecases.paquete.SincronizarPaqueteUseCase;
import com.logistica.domain.repositories.LogSincronizacionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PaqueteSincronizacionIntegrationTest {

    static WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());

    @DynamicPropertySource
    static void configurarWireMock(DynamicPropertyRegistry registry) {
        wireMockServer.start();
        registry.add("package-api.base-url", wireMockServer::baseUrl);
    }

    @Autowired
    private SincronizarPaqueteUseCase sincronizarUseCase;

    @Autowired
    private LogSincronizacionRepository logRepository;

    private UUID idRuta;
    private UUID idPaquete;

    @BeforeEach
    void setUp() {
        idRuta    = UUID.randomUUID();
        idPaquete = UUID.randomUUID();
        wireMockServer.resetRequests();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetAll();
    }

    // T007: respuesta exitosa HTTP 200 → JSON deserializado correctamente en PaqueteResponseDTO
    @Test
    void sincronizar_respuestaExitosa_estadoPersistidoCorrectamente() {
        wireMockServer.stubFor(get(urlPathEqualTo(
                "/route/" + idRuta + "/package/" + idPaquete))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"idPaquete\":\"" + idPaquete + "\",\"estado\":\"ENTREGADO\"}")));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.sincronizarEstado(idRuta, idPaquete);

        assertEquals("ENTREGADO", resultado.estado());
        assertEquals(100, resultado.porcentajePago());
        assertNotNull(resultado.mensaje());
    }

    // T011: WireMock HTTP 404 → LogSincronizacion registra error, detiene cálculo
    @Test
    void sincronizar_http404_registraErrorEnLogYNoProcesaPago() {
        wireMockServer.stubFor(get(urlPathEqualTo(
                "/route/" + idRuta + "/package/" + idPaquete))
                .willReturn(aResponse().withStatus(404)));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.sincronizarEstado(idRuta, idPaquete);

        assertEquals("PAQUETE_NO_ENCONTRADO", resultado.estado());
        assertNull(resultado.porcentajePago());

        // Verificar que el log fue registrado con código 404
        var logs = logRepository.findByIdPaquete(idPaquete);
        assertFalse(logs.isEmpty());
        assertEquals(404, logs.get(0).codigoRespuestaHTTP());
    }

    // T012: WireMock con delay de 3s → Resilience4j aborta a 2s, reintenta, marca PENDIENTE
    @Test
    void sincronizar_timeout_marcaPendienteSincronizacion() {
        wireMockServer.stubFor(get(urlPathEqualTo(
                "/route/" + idRuta + "/package/" + idPaquete))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(3000)   // 3 segundos — supera el timeout de 2s
                        .withBody("{\"idPaquete\":\"" + idPaquete + "\",\"estado\":\"ENTREGADO\"}")));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.sincronizarEstado(idRuta, idPaquete);

        assertEquals("PENDIENTE_SINCRONIZACION", resultado.estado());

        // Verificar que el fallo fue registrado en el log
        var logs = logRepository.findByIdPaquete(idPaquete);
        assertFalse(logs.isEmpty());
    }

    // T013: estado desconocido → omite cálculo de pago, registra consulta completa (SC-002)
    @Test
    void sincronizar_estadoNoMapeado_omiteCalculoYRegistraLog() {
        wireMockServer.stubFor(get(urlPathEqualTo(
                "/route/" + idRuta + "/package/" + idPaquete))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"idPaquete\":\"" + idPaquete + "\",\"estado\":\"EN_INSPECCION\"}")));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.sincronizarEstado(idRuta, idPaquete);

        assertEquals("ESTADO_NO_MAPEADO", resultado.estado());
        assertNull(resultado.porcentajePago());

        var logs = logRepository.findByIdPaquete(idPaquete);
        assertFalse(logs.isEmpty());
        assertEquals(200, logs.get(0).codigoRespuestaHTTP());
    }
}
