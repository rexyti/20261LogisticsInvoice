package com.logistica.RegistrarEstadoPago.unit;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.logistica.domain.registrarEstadoPago.exceptions.ErrorComunicacionBancoException;
import com.logistica.domain.registrarEstadoPago.exceptions.PagoRechazadoBancoException;
import com.logistica.domain.registrarEstadoPago.models.VoucherPago;
import com.logistica.infrastructure.registrarEstadoPago.adapters.BancoGatewayImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BancoGatewayImplTest {

    private WireMockServer wireMock;
    private BancoGatewayImpl gateway;

    private static final UUID ID_LIQUIDACION = UUID.randomUUID();
    private static final BigDecimal MONTO = new BigDecimal("2000.00");

    @BeforeEach
    void setUp() {
        wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMock.start();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(5));

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + wireMock.port())
                .defaultHeader("Content-Type", "application/json")
                .requestFactory(factory)
                .build();

        gateway = new BancoGatewayImpl(restClient);
    }

    @AfterEach
    void tearDown() {
        wireMock.stop();
    }

    @Test
    void registrarPago_bancoAprueba_retornaVoucher() {
        wireMock.stubFor(post(urlEqualTo("/pagos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "estado": "APROBADO",
                                  "voucher": "VCH-20260520-001",
                                  "fecha_procesamiento": "2026-05-20T10:00:00+00:00",
                                  "motivo": null
                                }
                                """)));

        VoucherPago result = gateway.registrarPago(ID_LIQUIDACION, MONTO);

        assertThat(result.numeroVoucher()).isEqualTo("VCH-20260520-001");
        assertThat(result.fechaProcesamiento()).isNotNull();

        wireMock.verify(postRequestedFor(urlEqualTo("/pagos"))
                .withRequestBody(matchingJsonPath("$.id_liquidacion", equalTo(ID_LIQUIDACION.toString())))
                .withRequestBody(matchingJsonPath("$.monto")));
    }

    @Test
    void registrarPago_bancoRechaza_lanzaPagoRechazadoBancoException() {
        wireMock.stubFor(post(urlEqualTo("/pagos"))
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

        assertThatThrownBy(() -> gateway.registrarPago(ID_LIQUIDACION, MONTO))
                .isInstanceOf(PagoRechazadoBancoException.class)
                .hasMessageContaining("Fondos insuficientes");
    }

    @Test
    void registrarPago_banco5xx_lanzaErrorComunicacionBancoException() {
        wireMock.stubFor(post(urlEqualTo("/pagos"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Internal Server Error\"}")));

        assertThatThrownBy(() -> gateway.registrarPago(ID_LIQUIDACION, MONTO))
                .isInstanceOf(ErrorComunicacionBancoException.class);
    }

    @Test
    void registrarPago_bancoInaccesible_lanzaErrorComunicacionBancoException() {
        wireMock.stop();

        assertThatThrownBy(() -> gateway.registrarPago(ID_LIQUIDACION, MONTO))
                .isInstanceOf(ErrorComunicacionBancoException.class);
    }

    @Test
    void registrarPago_respuestaVacia_lanzaErrorComunicacionBancoException() {
        wireMock.stubFor(post(urlEqualTo("/pagos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        assertThatThrownBy(() -> gateway.registrarPago(ID_LIQUIDACION, MONTO))
                .isInstanceOf(Exception.class);
    }
}
