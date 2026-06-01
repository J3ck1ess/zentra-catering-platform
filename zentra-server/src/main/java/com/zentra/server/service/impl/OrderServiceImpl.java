package com.zentra.server.service.impl;

import com.zentra.common.constant.*;
import com.zentra.common.context.AuthContext;
import com.zentra.common.exception.BusinessException;
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
import com.zentra.server.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Order service implementation
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    private final OrderItemMapper orderItemMapper;

    private final DishMapper dishMapper;

    /**
     * Redis runtime service
     */
    private final RedisService redisService;

    public OrderServiceImpl(
            OrderMapper orderMapper,
            OrderItemMapper orderItemMapper,
            DishMapper dishMapper,
            RedisService redisService
    ) {

        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.dishMapper = dishMapper;
        this.redisService = redisService;
    }

    /**
     * Build order create lock key
     */
    private String buildOrderCreateLockKey(
            Long merchantId,
            Long userId
    ) {

        return RedisKeyConstants.DISTRIBUTED_LOCK
                + "order:create:"
                + merchantId
                + ":"
                + userId;
    }

    /**
     * Create a new order
     */
    @Transactional
    @Override
    public void create(OrderCreateDTO dto) {

        // Set user ID
        Long userId = AuthContext.getCurrentUserId();

        // Set merchant ID
        Long merchantId = AuthContext.getCurrentMerchantId();

        // Build distributed lock
        String lockKey =
                buildOrderCreateLockKey(
                        merchantId,
                        userId
                );
        String lockValue =
                UUID.randomUUID().toString();

        // Try to acquire distributed lock
        boolean locked =
                redisService.tryLock(
                        lockKey,
                        lockValue,
                        RedisTtlConstants.DISTRIBUTED_LOCK_TTL
                );
        if (!locked) {

            log.warn(
                    "[ORDER] Duplicate order request detected. merchantId={}, userId={}, lockKey={}",
                    merchantId,
                    userId,
                    lockKey
            );

            throw new BusinessException(
                    ErrorCode.DUPLICATE_ORDER_REQUEST,
                    ErrorMessage.DUPLICATE_ORDER_REQUEST
            );
        }

        try {

            log.info(
                    "[ORDER] Distributed order lock acquired. merchantId={}, userId={}, lockKey={}",
                    merchantId,
                    userId,
                    lockKey
            );

            // Validate order items
            if (dto.getItems() == null || dto.getItems().isEmpty()) {

                throw new BusinessException(
                        ErrorCode.ORDER_ITEMS_EMPTY,
                        ErrorMessage.ORDER_ITEMS_EMPTY
                );
            }

            Map<Long, Dish> dishMap = new HashMap<>();

            log.info(
                    "[ORDER] Order item validation started. merchantId={}, userId={}, itemCount={}",
                    merchantId,
                    userId,
                    dto.getItems().size()
            );

            // Validate each order item
            for (OrderItemCreateDTO item : dto.getItems()) {

                Dish dish = dishMapper.findById(
                        item.getDishId(),
                        merchantId
                );
                AssertUtil.notNull(
                        dish,
                        ErrorCode.DISH_NOT_FOUND,
                        ErrorMessage.DISH_NOT_FOUND
                );

                // Check dish status
                if (dish.getStatus().equals(DishStatus.DISABLED)) {

                    log.warn(
                            "[ORDER] Disabled dish detected during order creation. merchantId={}, dishId={}",
                            merchantId,
                            dish.getId()
                    );

                    throw new BusinessException(
                            ErrorCode.DISH_DISABLED,
                            ErrorMessage.DISH_DISABLED
                    );
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

            log.info(
                    "[ORDER] Order amount calculated. merchantId={}, userId={}, totalAmount={}",
                    merchantId,
                    userId,
                    totalAmount
            );

            // Insert Order
            Order order = new Order();

            order.setMerchantId(merchantId);
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setStatus(OrderStatus.PENDING);

            // TODO: Generate order number
            order.setOrderNumber(String.valueOf(System.currentTimeMillis()));

            int orderRows = orderMapper.insert(order);
            AssertUtil.checkRows(
                    orderRows,
                    ErrorCode.ORDER_CREATE_FAILED,
                    ErrorMessage.ORDER_CREATE_FAILED
            );

            log.info(
                    "[ORDER] Order creation persisted. orderId={}, merchantId={}, userId={}",
                    order.getId(),
                    merchantId,
                    userId
            );

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
                AssertUtil.checkRows(
                        orderItemRows,
                        ErrorCode.ORDER_ITEM_CREATE_FAILED,
                        ErrorMessage.ORDER_ITEM_CREATE_FAILED
                );

                log.info(
                        "[ORDER] Order item persisted. orderId={}, dishId={}, quantity={}",
                        order.getId(),
                        dish.getId(),
                        item.getQuantity()
                );
            }

            log.info(
                    "[ORDER] Order creation completed. orderId={}, merchantId={}, userId={}",
                    order.getId(),
                    merchantId,
                    userId
            );
        } finally {

            log.info(
                    "[ORDER] Releasing distributed order lock. merchantId={}, userId={}, lockKey={}",
                    merchantId,
                    userId,
                    lockKey
            );

            redisService.unlock(
                    lockKey,
                    lockValue
            );
        }
    }

    /**
     * Query orders with pagination
     */
    @Override
    public PageResult<OrderPageDTO> page(OrderQueryDTO query) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        Integer page = query.getPage();
        Integer pageSize = query.getPageSize();
        int offset = (page - 1) * pageSize;
        log.info(
                "[ORDER] Order page query started. merchantId={}, page={}, pageSize={}, status={}",
                merchantId,
                page,
                pageSize,
                query.getStatus()
        );

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

        log.info(
                "[ORDER] Order page query completed. merchantId={}, total={}",
                merchantId,
                total
        );
        return new PageResult<>(total, records);
    }

    /**
     * Get order by id
     */
    @Override
    public OrderDetailDTO getById(Long id) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[ORDER] Order detail query started. merchantId={}, orderId={}",
                merchantId,
                id
        );

        // Get order
        Order order = orderMapper.findById(
                id,
                merchantId
        );

        // Check order existence
        AssertUtil.notNull(
                order,
                ErrorCode.ORDER_NOT_FOUND,
                ErrorMessage.ORDER_NOT_FOUND
        );

        // Query order items
        List<OrderItem> items =
                orderItemMapper.findByOrderId(
                        order.getId(),
                        merchantId
                );

        // Convert Order -> DTO
        OrderDetailDTO dto = new OrderDetailDTO();
        BeanUtils.copyProperties(order, dto);

        // Convert OrderItems -> DTO
        List<OrderItemDTO> itemDTOs =
                items.stream().map(item -> {

                    OrderItemDTO itemDTO =
                            new OrderItemDTO();

                    BeanUtils.copyProperties(item, itemDTO);

                    return itemDTO;

                }).toList();

        dto.setItems(itemDTOs);

        log.info(
                "[ORDER] Order detail query completed. merchantId={}, orderId={}, itemCount={}",
                merchantId,
                order.getId(),
                items.size()
        );
        return dto;
    }

    /**
     * Update order status
     */
    @Override
    public void updateStatus(Long orderId, Integer newStatus) {

        Long merchantId = AuthContext.getCurrentMerchantId();

        log.info(
                "[ORDER] Order status update started. merchantId={}, orderId={}, newStatus={}",
                merchantId,
                orderId,
                newStatus
        );

        // Validate new status
        if (!OrderStatus.isValid(newStatus)) {

            throw new BusinessException(
                    ErrorCode.ORDER_STATUS_INVALID,
                    ErrorMessage.ORDER_STATUS_INVALID
            );
        }

        // Get order
        Order order = orderMapper.findById(
                orderId,
                merchantId
        );

        // Check order existence
        AssertUtil.notNull(
                order,
                ErrorCode.ORDER_NOT_FOUND,
                ErrorMessage.ORDER_NOT_FOUND
        );

        Integer oldStatus = order.getStatus();

        // Verify status transition
        if (!OrderStatusFlow.canTransfer(oldStatus, newStatus)) {

            log.warn(
                    "[ORDER] Invalid order status transition detected. orderId={}, oldStatus={}, newStatus={}",
                    orderId,
                    oldStatus,
                    newStatus
            );

            throw new BusinessException(
                    ErrorCode.ORDER_STATUS_TRANSITION_INVALID,
                    ErrorMessage.ORDER_STATUS_TRANSITION_INVALID
            );
        }

        // Update status
        int rows = orderMapper.updateStatus(
                orderId,
                merchantId,
                newStatus
        );

        AssertUtil.checkRows(
                rows,
                ErrorCode.ORDER_UPDATE_FAILED,
                ErrorMessage.ORDER_UPDATE_FAILED
        );

        log.info(
                "[ORDER] Order status updated successfully. orderId={}, oldStatus={}, newStatus={}",
                orderId,
                oldStatus,
                newStatus
        );
    }
}
