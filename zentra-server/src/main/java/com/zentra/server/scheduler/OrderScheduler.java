package com.zentra.server.scheduler;

import com.zentra.common.constant.OrderStatus;
import com.zentra.server.entity.Order;
import com.zentra.server.mapper.OrderMapper;
import com.zentra.server.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {

    private final OrderService orderService;

    private final OrderMapper orderMapper;

    /**
     * Scan and cancel expired pending orders
     */
    @Scheduled(
            initialDelay = 60000,
            fixedDelay = 300000
    )
    public void cancelExpiredOrder() {

        log.info("[ORDER_SCHEDULER] Expired order scan started");

        List<Order> expiredOrders = orderMapper.findExpiredPendingOrders(OrderStatus.PENDING);

        for (Order order : expiredOrders) {

            orderService.autoCancelExpiredOrder(order.getId());
        }

        log.info(
                "[ORDER_SCHEDULER] Expired order scan completed. expiredOrderCount={}",
                expiredOrders.size()
        );
    }
}
