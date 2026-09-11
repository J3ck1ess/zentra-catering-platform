package com.zentra.server.service;

public interface KafkaIdempotencyService {

    /**
     * Try to mark an event as processed.
     *
     * @return true if the event was marked successfully,
     *         false if the event was already processed
     */
    boolean tryMarkProcessed(String eventId);
}