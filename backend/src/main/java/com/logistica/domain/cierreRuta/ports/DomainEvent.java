package com.logistica.domain.cierreRuta.ports;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime occurredOn();
}
