package com.zentra.server.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventConsumer {

    @KafkaListener(
            topics = "zentra.order.created",
            groupId = "zentra-order-service"
    )
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info(
                "[KAFKA] OrderCreatedEvent received. orderId={}, merchantId={}, userId={}",
                event.orderId(),
                event.merchantId(),
                event.userId()
        );
    }
}