package com.logistica.cierreRuta.infrastructure.time;

import com.logistica.cierreRuta.domain.ports.TimeProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CierreRutaSystemTimeProvider implements TimeProvider {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
