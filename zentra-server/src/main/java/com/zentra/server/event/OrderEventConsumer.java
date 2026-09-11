package com.zentra.server.event;

import com.zentra.server.service.KafkaIdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final KafkaIdempotencyService idempotencyService;

    private final OrderCreatedEventHandler eventHandler;

    @KafkaListener(
            topics = "zentra.order.created",
            groupId = "${spring.kafka.consumer.group-id}",
            autoStartup = "${spring.kafka.listener.auto-startup:true}"
    )

    public void handleOrderCreated(OrderCreatedEvent event) {

        String eventId = event.eventId();

        log.info(
                "[KAFKA] Processing OrderCreatedEvent. eventId={}, orderId={}, merchantId={}, userId={}",
                eventId,
                event.orderId(),
                event.merchantId(),
                event.userId()
        );

        eventHandler.handle(event);

        boolean marked = idempotencyService.tryMarkProcessed(eventId);

        if (!marked) {
            log.info(
                    "[KAFKA] Duplicate event ignored. eventId={}, orderId={}",
                    eventId,
                    event.orderId()
            );
            return;
        }

        log.info(
                "[KAFKA] OrderCreatedEvent processing completed. eventId={}, orderId={}",
                eventId,
                event.orderId()
        );
    }
}