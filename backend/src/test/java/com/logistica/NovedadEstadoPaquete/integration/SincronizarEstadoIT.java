package com.logistica.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.logistica.application.dtos.response.SincronizacionResultadoDTO;
import com.logistica.application.usecases.paquete.SincronizarPaqueteUseCase;
import com.logistica.domain.models.HistorialEstado;
import com.logistica.domain.models.LogSincronizacion;
import com.logistica.domain.repositories.HistorialRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests T007, T009, T010, T011, T012, T013.
 * WireMock simulates the Package Management Module on port 8089.
 * H2 in-memory DB is used (application-test.yml).
 */
@SpringBootTest
@ActiveProfiles("test")
class SincronizarEstadoIT {

    private static WireMockServer wireMock;

    @Autowired SincronizarPaqueteUseCase   sincronizarUseCase;
    @Autowired HistorialRepository         historialRepository;
    @Autowired LogSincronizacionRepository logSincronizacionRepository;

    @BeforeAll
    static void startWireMock() {
        wireMock = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8089));
        wireMock.start();
        configureFor("localhost", 8089);
    }

    @AfterAll
    static void stopWireMock() {
        wireMock.stop();
    }

    @BeforeEach
    void resetStubs() {
        wireMock.resetAll();
    }

    // -----------------------------------------------------------------------
    // T007 – HTTP 200: JSON deserializado correctamente → estado ENTREGADO
    // -----------------------------------------------------------------------
    @Test
    void t007_respuesta_exitosa_200_deserializa_estado_entregado() {
        stubFor(get(urlPathEqualTo("/route/1/package/10"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"10\", \"estado\": \"ENTREGADO\"}")));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.execute(1L, 10L);

        assertThat(resultado.resultado()).isEqualTo("EXITOSO");
        assertThat(resultado.estadoActual()).isEqualTo("ENTREGADO");
        assertThat(resultado.porcentajePago()).isEqualTo(100);
    }

    // -----------------------------------------------------------------------
    // T009 – SC-001: estadoActual en Paquete == estado en HistorialEstado
    // -----------------------------------------------------------------------
    @Test
    void t009_estado_persistido_coincide_con_respuesta_del_modulo() {
        stubFor(get(urlPathEqualTo("/route/1/package/20"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"20\", \"estado\": \"DEVUELTO\"}")));

        sincronizarUseCase.execute(1L, 20L);

        List<HistorialEstado> historial = historialRepository.findByIdPaqueteOrderByFechaDesc(20L);
        assertThat(historial).isNotEmpty();
        assertThat(historial.get(0).getEstado()).isEqualTo("DEVUELTO");
    }

    // -----------------------------------------------------------------------
    // T010 – FR-004: dos consultas distintas → historial crece, no sobrescribe
    // -----------------------------------------------------------------------
    @Test
    void t010_segunda_consulta_agrega_entrada_sin_sobrescribir_historial() {
        // Primera consulta: DEVUELTO
        stubFor(get(urlPathEqualTo("/route/1/package/30"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"30\", \"estado\": \"DEVUELTO\"}")));
        sincronizarUseCase.execute(1L, 30L);

        wireMock.resetAll();

        // Segunda consulta: ENTREGADO
        stubFor(get(urlPathEqualTo("/route/1/package/30"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"30\", \"estado\": \"ENTREGADO\"}")));
        sincronizarUseCase.execute(1L, 30L);

        List<HistorialEstado> historial = historialRepository.findByIdPaqueteOrderByFechaDesc(30L);
        assertThat(historial).hasSize(2);
        assertThat(historial.get(0).getEstado()).isEqualTo("ENTREGADO");
        assertThat(historial.get(1).getEstado()).isEqualTo("DEVUELTO");
    }

    // -----------------------------------------------------------------------
    // T011 – HTTP 404: log registrado, cálculo detenido
    // -----------------------------------------------------------------------
    @Test
    void t011_http_404_registra_error_en_log_y_detiene_calculo() {
        stubFor(get(urlPathEqualTo("/route/1/package/99"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Package not found\"}")));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.execute(1L, 99L);

        assertThat(resultado.resultado()).isEqualTo("PAQUETE_NO_ENCONTRADO");

        List<LogSincronizacion> logs = logSincronizacionRepository.findByIdPaquete(99L);
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getCodigoRespuestaHTTP()).isEqualTo(404);
    }

    // -----------------------------------------------------------------------
    // T012 – Timeout 3s → reintentos → PENDIENTE_SINCRONIZACION
    // -----------------------------------------------------------------------
    @Test
    void t012_delay_mayor_a_timeout_activa_retry_y_marca_pendiente() {
        // 3 second delay exceeds the 2s TimeLimiter configured in application-test.yml
        stubFor(get(urlPathEqualTo("/route/1/package/50"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"50\", \"estado\": \"ENTREGADO\"}")
                        .withFixedDelay(3000)));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.execute(1L, 50L);

        assertThat(resultado.resultado()).isEqualTo("PENDIENTE");
        assertThat(resultado.estadoActual()).isEqualTo("PENDIENTE_SINCRONIZACION");

        List<LogSincronizacion> logs = logSincronizacionRepository.findByIdPaquete(50L);
        assertThat(logs).isNotEmpty();
    }

    // -----------------------------------------------------------------------
    // T013 – Estado desconocido → omite cálculo, registra consulta en log
    // -----------------------------------------------------------------------
    @Test
    void t013_estado_desconocido_omite_calculo_pero_registra_log() {
        stubFor(get(urlPathEqualTo("/route/1/package/60"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"60\", \"estado\": \"EN_INSPECCION\"}")));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.execute(1L, 60L);

        assertThat(resultado.resultado()).isEqualTo("ESTADO_NO_MAPEADO");
        assertThat(resultado.porcentajePago()).isNull();

        List<LogSincronizacion> logs = logSincronizacionRepository.findByIdPaquete(60L);
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getCodigoRespuestaHTTP()).isEqualTo(200);
    }
}
