package com.logistica.conductor.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ValidarConductorIntegrationTest {

    static WireMockServer wireMockServer;

    static {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
    }

    @DynamicPropertySource
    static void configurarPropiedades(DynamicPropertyRegistry registry) {
        registry.add("app.servicios.conductores.base-url",
                () -> "http://localhost:" + wireMockServer.port());
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        @Order(0)
        SecurityFilterChain permitAllChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }

    @Autowired
    private TestRestTemplate restTemplate;

    private UUID idConductor;

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
        idConductor = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetAll();
    }

    @Test
    void validarConductor_activo_retorna200ConDatos() {
        wireMockServer.stubFor(get(urlEqualTo("/conductores/" + idConductor))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "conductor_id": "%s",
                                  "nombre": "Carlos Ruiz",
                                  "estado": "ACTIVO"
                                }
                                """.formatted(idConductor))));

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/v1/conductores/{id}/validar", Map.class, idConductor);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("nombre")).isEqualTo("Carlos Ruiz");
        assertThat(response.getBody().get("estado")).isEqualTo("ACTIVO");
        assertThat(response.getBody().get("conductor_id")).isEqualTo(idConductor.toString());
    }

    @Test
    void validarConductor_inactivo_retorna422() {
        wireMockServer.stubFor(get(urlEqualTo("/conductores/" + idConductor))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "conductor_id": "%s",
                                  "nombre": "Pedro Inactivo",
                                  "estado": "INACTIVO"
                                }
                                """.formatted(idConductor))));

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/v1/conductores/{id}/validar", Map.class, idConductor);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().get("codigo")).isEqualTo("CONDUCTOR_INACTIVO");
    }

    @Test
    void validarConductor_noEncontrado_retorna404() {
        wireMockServer.stubFor(get(urlEqualTo("/conductores/" + idConductor))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"not found\"}")));

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/v1/conductores/{id}/validar", Map.class, idConductor);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("codigo")).isEqualTo("RECURSO_NO_ENCONTRADO");
    }

    @Test
    void validarConductor_servicio5xx_retorna503() {
        wireMockServer.stubFor(get(urlEqualTo("/conductores/" + idConductor))
                .willReturn(aResponse().withStatus(500)));

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/v1/conductores/{id}/validar", Map.class, idConductor);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void validarConductor_verificaLlamadaAlServicioExterno() {
        wireMockServer.stubFor(get(urlEqualTo("/conductores/" + idConductor))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "conductor_id": "%s",
                                  "nombre": "Test",
                                  "estado": "ACTIVO"
                                }
                                """.formatted(idConductor))));

        restTemplate.getForEntity("/api/v1/conductores/{id}/validar", Map.class, idConductor);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/conductores/" + idConductor)));
    }
}
