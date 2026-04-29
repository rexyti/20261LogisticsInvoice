package com.logistica.cierreRuta.domain.ports;

public interface EventPublisher {
    void publish(Object event);
}
