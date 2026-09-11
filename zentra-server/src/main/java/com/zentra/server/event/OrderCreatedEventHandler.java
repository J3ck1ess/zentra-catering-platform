package com.zentra.server.event;

public interface OrderCreatedEventHandler {

    void handle(OrderCreatedEvent event);
}