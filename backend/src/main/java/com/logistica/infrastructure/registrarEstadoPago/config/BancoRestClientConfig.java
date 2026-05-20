package com.logistica.infrastructure.registrarEstadoPago.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class BancoRestClientConfig {

    @Value("${app.servicios.banco.base-url}")
    private String baseUrl;

    @Value("${app.servicios.banco.timeout-segundos:10}")
    private int timeoutSegundos;

    @Bean("bancoPagoRestClient")
    public RestClient bancoRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(timeoutSegundos));
        factory.setReadTimeout(Duration.ofSeconds(timeoutSegundos));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .requestFactory(factory)
                .build();
    }
}
