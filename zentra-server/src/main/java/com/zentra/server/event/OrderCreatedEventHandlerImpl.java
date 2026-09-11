package com.zentra.server.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCreatedEventHandlerImpl
        implements OrderCreatedEventHandler {

    @Override
    public void handle(OrderCreatedEvent event) {
        log.info(
                "[KAFKA] Executing OrderCreatedEvent business logic. eventId={}, orderId={}",
                event.eventId(),
                event.orderId()
        );

        // Business processing
    }
}