package com.logistica.infrastructure.shared.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "app.mock.tarifa")
@Getter
@Setter
public class TarifaMockProperties {
    private BigDecimal porParada;
    private BigDecimal recorridoCompleto;
}