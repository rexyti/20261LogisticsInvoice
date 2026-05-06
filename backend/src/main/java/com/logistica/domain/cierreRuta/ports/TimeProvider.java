package com.logistica.domain.cierreRuta.ports;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();
}
