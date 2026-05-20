package com.logistica.conductor.unit;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.logistica.domain.conductor.enums.EstadoConductor;
import com.logistica.domain.conductor.exceptions.ConductorNoEncontradoException;
import com.logistica.domain.conductor.models.Conductor;
import com.logistica.infrastructure.conductor.adapters.ConductorGatewayImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConductorGatewayImplTest {

    private WireMockServer wireMock;
    private ConductorGatewayImpl gateway;

    private static final UUID ID_CONDUCTOR = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMock.start();

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + wireMock.port())
                .defaultHeader("Content-Type", "application/json")
                .build();

        gateway = new ConductorGatewayImpl(restClient);
    }

    @AfterEach
    void tearDown() {
        wireMock.stop();
    }

    @Test
    void consultar_conductorActivo_retornaConductorMapeado() {
        wireMock.stubFor(get(urlEqualTo("/conductores/" + ID_CONDUCTOR))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "conductor_id": "%s",
                                  "nombre": "Luis Fernández",
                                  "estado": "ACTIVO"
                                }
                                """.formatted(ID_CONDUCTOR))));

        Conductor result = gateway.consultar(ID_CONDUCTOR);

        assertThat(result.getConductorId()).isEqualTo(ID_CONDUCTOR);
        assertThat(result.getNombre()).isEqualTo("Luis Fernández");
        assertThat(result.getEstado()).isEqualTo(EstadoConductor.ACTIVO);
        assertThat(result.estaActivo()).isTrue();
    }

    @Test
    void consultar_conductorInactivo_retornaConductorConEstadoInactivo() {
        wireMock.stubFor(get(urlEqualTo("/conductores/" + ID_CONDUCTOR))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "conductor_id": "%s",
                                  "nombre": "Pedro Gómez",
                                  "estado": "INACTIVO"
                                }
                                """.formatted(ID_CONDUCTOR))));

        Conductor result = gateway.consultar(ID_CONDUCTOR);

        assertThat(result.getEstado()).isEqualTo(EstadoConductor.INACTIVO);
        assertThat(result.estaActivo()).isFalse();
    }

    @Test
    void consultar_servicio404_lanzaConductorNoEncontradoException() {
        wireMock.stubFor(get(urlEqualTo("/conductores/" + ID_CONDUCTOR))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Conductor not found\"}")));

        assertThatThrownBy(() -> gateway.consultar(ID_CONDUCTOR))
                .isInstanceOf(ConductorNoEncontradoException.class);
    }

    @Test
    void consultar_servicio5xx_lanzaRuntimeException() {
        wireMock.stubFor(get(urlEqualTo("/conductores/" + ID_CONDUCTOR))
                .willReturn(aResponse()
                        .withStatus(500)));

        assertThatThrownBy(() -> gateway.consultar(ID_CONDUCTOR))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void consultar_servicioInaccesible_lanzaRuntimeException() {
        wireMock.stop();

        assertThatThrownBy(() -> gateway.consultar(ID_CONDUCTOR))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void consultar_verificaUrlCorrecta() {
        wireMock.stubFor(get(urlEqualTo("/conductores/" + ID_CONDUCTOR))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "conductor_id": "%s",
                                  "nombre": "Test",
                                  "estado": "ACTIVO"
                                }
                                """.formatted(ID_CONDUCTOR))));

        gateway.consultar(ID_CONDUCTOR);

        wireMock.verify(getRequestedFor(urlEqualTo("/conductores/" + ID_CONDUCTOR)));
    }
}
