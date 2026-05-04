package com.zentra.server.service.impl;

import com.zentra.common.constant.OrderStatus;
import com.zentra.common.result.PageResult;
import com.zentra.common.util.AssertUtil;
import com.zentra.server.context.UserContext;
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
import java.util.ArrayList;
import java.util.List;

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

        // Set merchant ID from current user context
        Long merchantId = UserContext.getCurrentUser();

        // Validate order items
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be empty");
        }

        List<Dish> dishes = new ArrayList<>();

        // Validate each order item
        for (OrderItemCreateDTO item : dto.getItems()) {

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Invalid quantity");
            }

            Dish dish = dishMapper.findById(item.getDishId(), merchantId);
            AssertUtil.notNull(dish, "Dish not found");

            dishes.add(dish);
        }

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (int i = 0; i < dishes.size(); i++) {

            Dish dish = dishes.get(i);
            OrderItemCreateDTO item = dto.getItems().get(i);

            BigDecimal amount = dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            totalAmount = totalAmount.add(amount);
        }

        // Insert Order
        Order order = new Order();

        order.setMerchantId(merchantId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING); // Pending

        order.setOrderNumber(String.valueOf(System.currentTimeMillis()));

        orderMapper.insert(order);

        // Insert Order Items
        for (int i = 0; i < dishes.size(); i++) {

            // Get dish and order item
            Dish dish = dishes.get(i);
            OrderItemCreateDTO item = dto.getItems().get(i);
            BigDecimal amount = dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderItem orderItem = new OrderItem();

            // Set order item properties
            orderItem.setOrderId(order.getId());
            orderItem.setDishId(dish.getId());
            orderItem.setDishName(dish.getName());
            orderItem.setPrice(dish.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setAmount(amount);

            orderItemMapper.insert(orderItem);

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

        Long merchantId = UserContext.getCurrentUser();

        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;

        // Query data
        List<Order> orders = orderMapper.findPage(
                query.getStatus(),
                merchantId,
                offset,
                pageSize
        );

        List<OrderPageDTO> list = orders.stream().map(order -> {
            OrderPageDTO dto = new OrderPageDTO();
            BeanUtils.copyProperties(order, dto);
            return dto;
        }).toList();

        // Query total count
        Long total = orderMapper.count(
                query.getStatus(),
                merchantId
        );

        return new PageResult<>(total, list);
    }

    /**
     * Get order by id
     *
     * @param id
     * @return
     */
    @Override
    public OrderDetailDTO getById(Long id) {

        Long merchantId = UserContext.getCurrentUser();

        // Get order
        Order order = orderMapper.findById(id, merchantId);
        AssertUtil.notNull(order, "Order not found");

        // Get order items
        List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());

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
}
