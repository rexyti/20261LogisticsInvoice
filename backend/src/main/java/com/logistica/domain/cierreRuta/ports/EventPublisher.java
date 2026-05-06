package com.logistica.domain.cierreRuta.ports;

public interface EventPublisher {
    void publish(Object event);
}
