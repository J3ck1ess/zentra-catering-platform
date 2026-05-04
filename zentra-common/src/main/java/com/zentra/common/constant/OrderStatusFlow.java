package com.zentra.common.constant;

import java.util.Map;
import java.util.Set;

public class OrderStatusFlow {

    private static final Map<Integer, Set<Integer>> FLOW = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
            OrderStatus.PAID, Set.of(OrderStatus.COMPLETED),
            OrderStatus.COMPLETED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    /**
     * Check if status transition is allowed
     */
    public static boolean canTransfer(int from, int to) {

        return FLOW.getOrDefault(from, Set.of()).contains(to);
    }

    private OrderStatusFlow() {
        // prevent instantiation
    }
}
