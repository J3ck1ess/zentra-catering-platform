package com.zentra.server.event;

public record OrderCreatedEvent(
        String eventId,
        Long orderId,
        Long merchantId,
        Long userId
) {
}