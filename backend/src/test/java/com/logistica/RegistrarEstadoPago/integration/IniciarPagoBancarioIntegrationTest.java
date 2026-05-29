package com.logistica.RegistrarEstadoPago.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.logistica.domain.registrarEstadoPago.ports.LiquidacionPagoPort;
import com.logistica.infrastructure.registrarEstadoPago.persistence.repositories.RegistrarEstadoPagoEstadoPagoJpaRepository;
import com.logistica.infrastructure.registrarEstadoPago.persistence.repositories.RegistrarEstadoPagoPagoJpaRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class IniciarPagoBancarioIntegrationTest {

    // WireMock arranca antes del contexto de Spring para que @DynamicPropertySource pueda leer el puerto
    static WireMockServer wireMockServer;

    static {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
    }

    @DynamicPropertySource
    static void configurarPropiedades(DynamicPropertyRegistry registry) {
        registry.add("app.servicios.banco.base-url",
                () -> "http://localhost:" + wireMockServer.port());
    }

    @MockBean private LiquidacionPagoPort liquidacionPagoPort;

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

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private RegistrarEstadoPagoPagoJpaRepository pagoJpaRepository;
    @Autowired private RegistrarEstadoPagoEstadoPagoJpaRepository estadoPagoJpaRepository;

    private UUID idLiquidacion;

    @BeforeEach
    void setUp() {
        estadoPagoJpaRepository.deleteAll();
        pagoJpaRepository.deleteAll();
        wireMockServer.resetAll();
        idLiquidacion = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetAll();
    }

    @Test
    void iniciarPago_bancoAprueba_retorna201ConVoucher() {
        when(liquidacionPagoPort.existe(idLiquidacion)).thenReturn(true);

        wireMockServer.stubFor(post(urlEqualTo("/pagos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "estado": "APROBADO",
                                  "voucher": "VCH-INT-001",
                                  "fecha_procesamiento": "2026-05-20T10:00:00+00:00",
                                  "motivo": null
                                }
                                """)));

        Map<String, Object> request = Map.of("monto", "1500.00");

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/liquidaciones/{id}/pago/banco",
                HttpMethod.POST,
                new HttpEntity<>(request, jsonHeaders()),
                Map.class,
                idLiquidacion);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsKey("numero_voucher");
        assertThat(response.getBody().get("numero_voucher")).isEqualTo("VCH-INT-001");
        assertThat(response.getBody().get("estado_pago")).isEqualTo("PAGADO");

        // verifica que el pago fue persistido
        assertThat(pagoJpaRepository.findAll()).hasSize(1);
        assertThat(estadoPagoJpaRepository.findAll()).hasSize(1);

        // verifica que se marcó como pagada
        verify(liquidacionPagoPort).marcarComoPagada(idLiquidacion);
    }

    @Test
    void iniciarPago_bancoRechaza_retorna422() {
        when(liquidacionPagoPort.existe(idLiquidacion)).thenReturn(true);

        wireMockServer.stubFor(post(urlEqualTo("/pagos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "estado": "RECHAZADO",
                                  "voucher": null,
                                  "fecha_procesamiento": null,
                                  "motivo": "Fondos insuficientes"
                                }
                                """)));

        Map<String, Object> request = Map.of("monto", "1500.00");

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/liquidaciones/{id}/pago/banco",
                HttpMethod.POST,
                new HttpEntity<>(request, jsonHeaders()),
                Map.class,
                idLiquidacion);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().get("codigo")).isEqualTo("PAGO_RECHAZADO");

        // verifica que no se persistió nada
        assertThat(pagoJpaRepository.findAll()).isEmpty();
        verify(liquidacionPagoPort, never()).marcarComoPagada(any());
    }

    @Test
    void iniciarPago_liquidacionInexistente_retorna404() {
        when(liquidacionPagoPort.existe(idLiquidacion)).thenReturn(false);

        Map<String, Object> request = Map.of("monto", "1500.00");

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/liquidaciones/{id}/pago/banco",
                HttpMethod.POST,
                new HttpEntity<>(request, jsonHeaders()),
                Map.class,
                idLiquidacion);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        wireMockServer.verify(0, postRequestedFor(urlEqualTo("/pagos")));
    }

    @Test
    void iniciarPago_banco5xx_retorna503() {
        when(liquidacionPagoPort.existe(idLiquidacion)).thenReturn(true);

        wireMockServer.stubFor(post(urlEqualTo("/pagos"))
                .willReturn(aResponse().withStatus(500)));

        Map<String, Object> request = Map.of("monto", "1500.00");

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/liquidaciones/{id}/pago/banco",
                HttpMethod.POST,
                new HttpEntity<>(request, jsonHeaders()),
                Map.class,
                idLiquidacion);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().get("codigo")).isEqualTo("SISTEMA_NO_DISPONIBLE");
    }

    @Test
    void iniciarPago_montoNulo_retorna400() {
        Map<String, Object> request = Map.of();

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/liquidaciones/{id}/pago/banco",
                HttpMethod.POST,
                new HttpEntity<>(request, jsonHeaders()),
                Map.class,
                idLiquidacion);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        wireMockServer.verify(0, postRequestedFor(urlEqualTo("/pagos")));
    }

    @Test
    void iniciarPago_verificaPayloadEnviadoAlBanco() {
        when(liquidacionPagoPort.existe(idLiquidacion)).thenReturn(true);

        wireMockServer.stubFor(post(urlEqualTo("/pagos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "estado": "APROBADO",
                                  "voucher": "VCH-VERIFY",
                                  "fecha_procesamiento": "2026-05-20T12:00:00+00:00",
                                  "motivo": null
                                }
                                """)));

        Map<String, Object> request = Map.of("monto", "3750.50");

        restTemplate.exchange(
                "/api/v1/liquidaciones/{id}/pago/banco",
                HttpMethod.POST,
                new HttpEntity<>(request, jsonHeaders()),
                Map.class,
                idLiquidacion);

        wireMockServer.verify(postRequestedFor(urlEqualTo("/pagos"))
                .withRequestBody(matchingJsonPath("$.id_liquidacion", equalTo(idLiquidacion.toString())))
                .withRequestBody(matchingJsonPath("$.monto")));
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
