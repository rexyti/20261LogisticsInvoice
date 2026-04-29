package com.logistica.cierreRuta.domain.ports;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime occurredOn();
}
