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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class PaqueteSincronizacionIntegrationTest {

    static WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());

    @DynamicPropertySource
    static void configurarWireMock(DynamicPropertyRegistry registry) {
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
        }
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
        idRuta = UUID.randomUUID();
        idPaquete = UUID.randomUUID();
        wireMockServer.resetRequests();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetAll();
    }

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

    @Test
    void sincronizar_http404_registraErrorEnLogYNoProcesaPago() {
        wireMockServer.stubFor(get(urlPathEqualTo(
                "/route/" + idRuta + "/package/" + idPaquete))
                .willReturn(aResponse().withStatus(404)));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.sincronizarEstado(idRuta, idPaquete);

        assertEquals("PAQUETE_NO_ENCONTRADO", resultado.estado());
        assertNull(resultado.porcentajePago());

        var logs = logRepository.findByIdPaquete(idPaquete);
        assertFalse(logs.isEmpty());
        assertEquals(404, logs.get(0).codigoRespuestaHTTP());
    }

    @Test
    void sincronizar_timeout_marcaPendienteSincronizacion() {
        wireMockServer.stubFor(get(urlPathEqualTo(
                "/route/" + idRuta + "/package/" + idPaquete))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(3000)
                        .withBody("{\"idPaquete\":\"" + idPaquete + "\",\"estado\":\"ENTREGADO\"}")));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.sincronizarEstado(idRuta, idPaquete);

        assertEquals("PENDIENTE_SINCRONIZACION", resultado.estado());
        assertFalse(logRepository.findByIdPaquete(idPaquete).isEmpty());
    }

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
