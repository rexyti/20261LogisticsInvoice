package com.logistica.NovedadEstadoPaquete.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.logistica.application.novedadEstadoPaquete.dtos.response.SincronizacionResultadoDTO;
import com.logistica.application.novedadEstadoPaquete.usecases.paquete.SincronizarPaqueteUseCase;
import com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities.HistorialEstadoEntity;
import com.logistica.infrastructure.novedadEstadoPaquete.persistence.entities.LogSincronizacionEntity;
import com.logistica.infrastructure.novedadEstadoPaquete.persistence.repositories.HistorialJpaRepository;
import com.logistica.infrastructure.novedadEstadoPaquete.persistence.repositories.LogSincronizacionJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SincronizarEstadoIT {

    // UUIDs fijos para garantizar reproducibilidad entre ejecuciones
    private static final UUID RUTA_ID      = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID PAQUETE_T007 = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final UUID PAQUETE_T009 = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final UUID PAQUETE_T010 = UUID.fromString("00000000-0000-0000-0000-000000000030");
    private static final UUID PAQUETE_T011 = UUID.fromString("00000000-0000-0000-0000-000000000099");
    private static final UUID PAQUETE_T012 = UUID.fromString("00000000-0000-0000-0000-000000000050");
    private static final UUID PAQUETE_T013 = UUID.fromString("00000000-0000-0000-0000-000000000060");

    private static WireMockServer wireMock;

    @Autowired SincronizarPaqueteUseCase   sincronizarUseCase;
    @Autowired HistorialJpaRepository         historialRepository;
    @Autowired LogSincronizacionJpaRepository logSincronizacionRepository;

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
        stubFor(get(urlPathEqualTo("/route/" + RUTA_ID + "/package/" + PAQUETE_T007))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"" + PAQUETE_T007 + "\", \"estado\": \"ENTREGADO\"}")));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.execute(RUTA_ID, PAQUETE_T007);

        assertThat(resultado.resultado()).isEqualTo("EXITOSO");
        assertThat(resultado.estadoActual()).isEqualTo("ENTREGADO");
        assertThat(resultado.porcentajePago()).isEqualTo(100);
    }

    // -----------------------------------------------------------------------
    // T009 – SC-001: estadoActual en NovedadEstadoPaquetePaquete == estado en HistorialEstado
    // -----------------------------------------------------------------------
    @Test
    void t009_estado_persistido_coincide_con_respuesta_del_modulo() {
        stubFor(get(urlPathEqualTo("/route/" + RUTA_ID + "/package/" + PAQUETE_T009))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"" + PAQUETE_T009 + "\", \"estado\": \"DEVUELTO\"}")));

        sincronizarUseCase.execute(RUTA_ID, PAQUETE_T009);

        List<HistorialEstadoEntity> historial = historialRepository
                .findByIdPaquete(PAQUETE_T009, PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "fecha")))
                .getContent();
        assertThat(historial).isNotEmpty();
        assertThat(historial.get(0).getEstado()).isEqualTo("DEVUELTO");
    }

    // -----------------------------------------------------------------------
    // T010 – FR-004: dos consultas distintas → historial crece, no sobrescribe
    // -----------------------------------------------------------------------
    @Test
    void t010_segunda_consulta_agrega_entrada_sin_sobrescribir_historial() {
        // Primera consulta: DEVUELTO
        stubFor(get(urlPathEqualTo("/route/" + RUTA_ID + "/package/" + PAQUETE_T010))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"" + PAQUETE_T010 + "\", \"estado\": \"DEVUELTO\"}")));
        sincronizarUseCase.execute(RUTA_ID, PAQUETE_T010);

        wireMock.resetAll();

        // Segunda consulta: ENTREGADO
        stubFor(get(urlPathEqualTo("/route/" + RUTA_ID + "/package/" + PAQUETE_T010))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"" + PAQUETE_T010 + "\", \"estado\": \"ENTREGADO\"}")));
        sincronizarUseCase.execute(RUTA_ID, PAQUETE_T010);

        List<HistorialEstadoEntity> historial = historialRepository
                .findByIdPaquete(PAQUETE_T010, PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "fecha")))
                .getContent();
        assertThat(historial).hasSize(2);
        assertThat(historial.get(0).getEstado()).isEqualTo("ENTREGADO");
        assertThat(historial.get(1).getEstado()).isEqualTo("DEVUELTO");
    }

    // -----------------------------------------------------------------------
    // T011 – HTTP 404: log registrado, cálculo detenido
    // -----------------------------------------------------------------------
    @Test
    void t011_http_404_registra_error_en_log_y_detiene_calculo() {
        stubFor(get(urlPathEqualTo("/route/" + RUTA_ID + "/package/" + PAQUETE_T011))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Package not found\"}")));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.execute(RUTA_ID, PAQUETE_T011);

        assertThat(resultado.resultado()).isEqualTo("PAQUETE_NO_ENCONTRADO");

        List<LogSincronizacionEntity> logs = logSincronizacionRepository
                .findByIdPaquete(PAQUETE_T011, Pageable.unpaged()).getContent();
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getCodigoRespuestaHTTP()).isEqualTo(404);
    }

    // -----------------------------------------------------------------------
    // T012 – Timeout 3s → reintentos → PENDIENTE_SINCRONIZACION
    // -----------------------------------------------------------------------
    @Test
    void t012_delay_mayor_a_timeout_activa_retry_y_marca_pendiente() {
        stubFor(get(urlPathEqualTo("/route/" + RUTA_ID + "/package/" + PAQUETE_T012))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"" + PAQUETE_T012 + "\", \"estado\": \"ENTREGADO\"}")
                        .withFixedDelay(3000)));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.execute(RUTA_ID, PAQUETE_T012);

        assertThat(resultado.resultado()).isEqualTo("PENDIENTE");
        assertThat(resultado.estadoActual()).isEqualTo("PENDIENTE_SINCRONIZACION");

        List<LogSincronizacionEntity> logs = logSincronizacionRepository
                .findByIdPaquete(PAQUETE_T012, Pageable.unpaged()).getContent();
        assertThat(logs).isNotEmpty();
    }

    // -----------------------------------------------------------------------
    // T013 – Estado desconocido → omite cálculo, registra consulta en log
    // -----------------------------------------------------------------------
    @Test
    void t013_estado_desconocido_omite_calculo_pero_registra_log() {
        stubFor(get(urlPathEqualTo("/route/" + RUTA_ID + "/package/" + PAQUETE_T013))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id_paquete\": \"" + PAQUETE_T013 + "\", \"estado\": \"EN_INSPECCION\"}")));

        SincronizacionResultadoDTO resultado = sincronizarUseCase.execute(RUTA_ID, PAQUETE_T013);

        assertThat(resultado.resultado()).isEqualTo("ESTADO_NO_MAPEADO");
        assertThat(resultado.porcentajePago()).isNull();

        List<LogSincronizacionEntity> logs = logSincronizacionRepository
                .findByIdPaquete(PAQUETE_T013, Pageable.unpaged()).getContent();
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getCodigoRespuestaHTTP()).isEqualTo(200);
    }
}