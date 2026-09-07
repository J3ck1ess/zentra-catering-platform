package com.zentra.server.event;

public record OrderCreatedEvent(
        Long orderId,
        Long merchantId,
        Long userId
) {
}