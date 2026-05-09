package com.zentra.server.service.impl;

import com.zentra.common.constant.DishStatus;
import com.zentra.common.constant.OrderStatus;
import com.zentra.common.constant.OrderStatusFlow;
import com.zentra.common.context.AuthContext;
import com.zentra.common.result.PageResult;
import com.zentra.common.util.AssertUtil;
import com.zentra.server.dto.*;
import com.zentra.server.entity.Dish;
import com.zentra.server.entity.Order;
import com.zentra.server.entity.OrderItem;
import com.zentra.server.mapper.DishMapper;
import com.zentra.server.mapper.OrderItemMapper;
import com.zentra.server.mapper.OrderMapper;
import com.zentra.server.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Order service implementation
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final DishMapper dishMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper, DishMapper dishMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.dishMapper = dishMapper;
    }

    /**
     * Create a new order
     *
     * @param dto
     * @return
     */
    @Transactional
    @Override
    public void create(OrderCreateDTO dto) {

        // Set user ID
        Long userId = AuthContext.getCurrentUserId();

        // Set merchant ID
        Long merchantId = AuthContext.getCurrentMerchantId();

        // Validate order items
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be empty");
        }

        Map<Long, Dish> dishMap = new HashMap<>();

        // Validate each order item
        for (OrderItemCreateDTO item : dto.getItems()) {

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Invalid quantity");
            }

            Dish dish = dishMapper.findById(item.getDishId(), merchantId);
            AssertUtil.notNull(dish, "Dish not found");

            // Check dish status
            if (dish.getStatus().equals(DishStatus.DISABLED)) {
                throw new IllegalArgumentException("Dish is disabled");
            }

            dishMap.put(dish.getId(), dish);
        }

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemCreateDTO item : dto.getItems()) {

            Dish dish = dishMap.get(item.getDishId());

            BigDecimal amount =
                    dish.getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    );

            totalAmount = totalAmount.add(amount);
        }

        // Insert Order
        Order order = new Order();

        order.setMerchantId(merchantId);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING); // Pending

        // TODO: Generate order number
        order.setOrderNumber(String.valueOf(System.currentTimeMillis()));

        int orderRows = orderMapper.insert(order);
        AssertUtil.checkRows(orderRows, "Failed to create order");

        // Insert Order Items
        for (OrderItemCreateDTO item : dto.getItems()) {

            // Get dish and order item
            Dish dish = dishMap.get(item.getDishId());

            BigDecimal amount =
                    dish.getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    );

            OrderItem orderItem = new OrderItem();

            // Set order item properties
            orderItem.setMerchantId(merchantId);
            orderItem.setOrderId(order.getId());
            orderItem.setDishId(dish.getId());
            orderItem.setDishName(dish.getName());
            orderItem.setPrice(dish.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setAmount(amount);

            int orderItemRows = orderItemMapper.insert(orderItem);
            AssertUtil.checkRows(orderItemRows, "Failed to create order item");

        }
    }

    /**
     * Query orders with pagination
     *
     * @param query
     * @return
     */
    @Override
    public PageResult<OrderPageDTO> list(OrderQueryDTO query) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;

        // Query data
        List<Order> list = orderMapper.findPage(
                query.getStatus(),
                merchantId,
                offset,
                pageSize
        );

        List<OrderPageDTO> records = list.stream().map(order -> {
            OrderPageDTO dto = new OrderPageDTO();
            BeanUtils.copyProperties(order, dto);
            return dto;
        }).toList();

        // Query total count
        Long total = orderMapper.count(
                query.getStatus(),
                merchantId
        );

        return new PageResult<>(total, records);
    }

    /**
     * Get order by id
     *
     * @param id
     * @return
     */
    @Override
    public OrderDetailDTO getById(Long id) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Get order
        Order order = orderMapper.findById(id, merchantId);
        AssertUtil.notNull(order, "Order not found");

        // Get order items
        List<OrderItem> items =
                orderItemMapper.findByOrderId(
                        order.getId(),
                        merchantId
                );

        // Order -> DTO
        OrderDetailDTO dto = new OrderDetailDTO();
        BeanUtils.copyProperties(order, dto);

        // Order items -> DTO
        List<OrderItemDTO> itemDTOs = items.stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            BeanUtils.copyProperties(item, itemDTO);
            return itemDTO;
        }).toList();

        dto.setItems(itemDTOs);
        return dto;
    }

    /**
     * Update order status
     *
     * @param orderId
     * @param newStatus
     */
    @Override
    public void updateStatus(Long orderId, Integer newStatus) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        // Validate new status
        if (!OrderStatus.isValid(newStatus)) {

            throw new IllegalArgumentException("Invalid order status");
        }

        // Get order
        Order order = orderMapper.findById(orderId, merchantId);
        AssertUtil.notNull(order, "Order not found");

        Integer oldStatus = order.getStatus();

        // Verify status transition
        if (!OrderStatusFlow.canTransfer(oldStatus, newStatus)) {

            throw new IllegalArgumentException(
                    "Invalid status transition: "
                            + oldStatus
                            + " -> "
                            + newStatus
            );
        }

        // Update status
        int rows = orderMapper.updateStatus(orderId, merchantId, newStatus);
        AssertUtil.checkRows(rows, "Update failed");
    }
}
