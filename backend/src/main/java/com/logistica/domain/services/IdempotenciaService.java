package com.logistica.domain.services;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class IdempotenciaService {

    private final Set<UUID> processedEvents = Collections.synchronizedSet(new HashSet<>());

    public boolean isEventProcessed(UUID eventId) {
        return processedEvents.contains(eventId);
    }

    public void markEventAsProcessed(UUID eventId) {
        processedEvents.add(eventId);
    }
}
