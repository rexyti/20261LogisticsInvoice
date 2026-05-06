package com.logistica.infrastructure.cierreRuta.time;

import com.logistica.domain.cierreRuta.ports.TimeProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SystemTimeProvider implements TimeProvider {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
