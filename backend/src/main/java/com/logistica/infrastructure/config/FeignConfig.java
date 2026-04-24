package com.logistica.infrastructure.config;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    // Connection pooling via timeouts; timeout de 2s alineado con Resilience4j TimeLimiter
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(500, TimeUnit.MILLISECONDS, 2000, TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
