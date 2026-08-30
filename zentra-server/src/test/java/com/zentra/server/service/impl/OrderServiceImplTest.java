package com.zentra.server.service.impl;

import com.zentra.common.constant.DishStatus;
import com.zentra.common.constant.ErrorCode;
import com.zentra.common.constant.ErrorMessage;
import com.zentra.common.constant.OrderStatus;
import com.zentra.common.context.AuthContext;
import com.zentra.common.exception.BusinessException;
import com.zentra.common.result.PageResult;
import com.zentra.server.dto.*;
import com.zentra.server.entity.Dish;
import com.zentra.server.entity.Order;
import com.zentra.server.entity.OrderItem;
import com.zentra.server.mapper.DishMapper;
import com.zentra.server.mapper.OrderItemMapper;
import com.zentra.server.mapper.OrderMapper;
import com.zentra.server.service.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private DishMapper dishMapper;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private OrderServiceImpl orderService;

    // ==================== Create ====================
    @Test
    void create_shouldCreateOrderSuccessfully() {
        Long userId = 1L;
        Long merchantId = 1L;
        Long dishId = 10L;

        OrderItemCreateDTO itemDTO = new OrderItemCreateDTO();
        itemDTO.setDishId(dishId);
        itemDTO.setQuantity(2);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setItems(List.of(itemDTO));

        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setMerchantId(merchantId);
        dish.setName("Kung Pao Chicken");
        dish.setPrice(new BigDecimal("15.99"));
        dish.setStatus(DishStatus.ENABLED);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.setIfAbsent(
                    anyString(),
                    anyLong(),
                    any()
            )).thenReturn(true);

            when(redisService.tryLock(
                    anyString(),
                    anyString(),
                    any()
            )).thenReturn(true);

            when(dishMapper.findById(
                    dishId,
                    merchantId
            )).thenReturn(dish);

            when(orderMapper.insert(any(Order.class)))
                    .thenAnswer(invocation -> {
                        Order order = invocation.getArgument(0);
                        order.setId(100L);
                        return 1;
                    });

            when(orderItemMapper.insert(any(OrderItem.class)))
                    .thenReturn(1);

            orderService.create(dto);

            verify(dishMapper)
                    .findById(
                            dishId,
                            merchantId
                    );

            verify(orderMapper)
                    .insert(
                            argThat(order ->
                                    merchantId.equals(order.getMerchantId())
                                            && userId.equals(order.getUserId())
                                            && new BigDecimal("31.98")
                                            .compareTo(order.getTotalAmount()) == 0
                                            && order.getStatus() == OrderStatus.PENDING
                                            && order.getOrderNumber() != null
                            )
                    );

            verify(orderItemMapper)
                    .insert(
                            argThat(orderItem ->
                                    merchantId.equals(orderItem.getMerchantId())
                                            && Long.valueOf(100L)
                                            .equals(orderItem.getOrderId())
                                            && dishId.equals(orderItem.getDishId())
                                            && "Kung Pao Chicken".equals(
                                            orderItem.getDishName()
                                    )
                                            && new BigDecimal("15.99")
                                            .compareTo(
                                                    orderItem.getPrice()
                                            ) == 0
                                            && Integer.valueOf(2).equals(
                                            orderItem.getQuantity()
                                    )
                                            && new BigDecimal("31.98")
                                            .compareTo(
                                                    orderItem.getAmount()
                                            ) == 0
                            )
                    );

            verify(redisService)
                    .unlock(
                            anyString(),
                            anyString()
                    );
        }
    }

    @Test
    void create_shouldRejectDuplicateOrderRequest() {
        Long userId = 1L;
        Long merchantId = 1L;

        OrderItemCreateDTO itemDTO = new OrderItemCreateDTO();
        itemDTO.setDishId(10L);
        itemDTO.setQuantity(2);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setItems(List.of(itemDTO));

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.setIfAbsent(
                    anyString(),
                    anyLong(),
                    any()
            )).thenReturn(false);

            assertThatThrownBy(() ->
                    orderService.create(dto)
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.DUPLICATE_ORDER_REQUEST,
                            ErrorMessage.DUPLICATE_ORDER_REQUEST
                    )
            );

            verify(redisService)
                    .setIfAbsent(
                            anyString(),
                            anyLong(),
                            any()
                    );

            verify(redisService, never())
                    .tryLock(
                            anyString(),
                            anyString(),
                            any()
                    );

            verify(dishMapper, never())
                    .findById(
                            anyLong(),
                            anyLong()
                    );

            verify(orderMapper, never())
                    .insert(any(Order.class));

            verify(orderItemMapper, never())
                    .insert(any(OrderItem.class));

            verify(redisService, never())
                    .unlock(
                            anyString(),
                            anyString()
                    );
        }
    }

    @Test
    void create_shouldRejectWhenOrderCreateLockCannotBeAcquired() {
        Long userId = 1L;
        Long merchantId = 1L;

        OrderItemCreateDTO itemDTO = new OrderItemCreateDTO();
        itemDTO.setDishId(10L);
        itemDTO.setQuantity(2);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setItems(List.of(itemDTO));

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.setIfAbsent(
                    anyString(),
                    anyLong(),
                    any()
            )).thenReturn(true);

            when(redisService.tryLock(
                    anyString(),
                    anyString(),
                    any()
            )).thenReturn(false);

            assertThatThrownBy(() ->
                    orderService.create(dto)
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.DUPLICATE_ORDER_REQUEST,
                            ErrorMessage.DUPLICATE_ORDER_REQUEST
                    )
            );

            verify(redisService)
                    .setIfAbsent(
                            anyString(),
                            anyLong(),
                            any()
                    );

            verify(redisService)
                    .tryLock(
                            anyString(),
                            anyString(),
                            any()
                    );

            verify(dishMapper, never())
                    .findById(
                            anyLong(),
                            anyLong()
                    );

            verify(orderMapper, never())
                    .insert(any(Order.class));

            verify(orderItemMapper, never())
                    .insert(any(OrderItem.class));

            verify(redisService, never())
                    .unlock(
                            anyString(),
                            anyString()
                    );
        }
    }

    @Test
    void create_shouldRejectWhenOrderItemsAreEmpty() {
        Long userId = 1L;
        Long merchantId = 1L;

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setItems(List.of());

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.setIfAbsent(
                    anyString(),
                    anyLong(),
                    any()
            )).thenReturn(true);

            when(redisService.tryLock(
                    anyString(),
                    anyString(),
                    any()
            )).thenReturn(true);

            assertThatThrownBy(() ->
                    orderService.create(dto)
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.ORDER_ITEMS_EMPTY,
                            ErrorMessage.ORDER_ITEMS_EMPTY
                    )
            );

            verify(redisService)
                    .setIfAbsent(
                            anyString(),
                            anyLong(),
                            any()
                    );

            verify(redisService)
                    .tryLock(
                            anyString(),
                            anyString(),
                            any()
                    );

            verify(dishMapper, never())
                    .findById(
                            anyLong(),
                            anyLong()
                    );

            verify(orderMapper, never())
                    .insert(any(Order.class));

            verify(orderItemMapper, never())
                    .insert(any(OrderItem.class));

            verify(redisService)
                    .unlock(
                            anyString(),
                            anyString()
                    );
        }
    }

    @Test
    void create_shouldRejectWhenDishNotFound() {
        Long userId = 1L;
        Long merchantId = 1L;
        Long dishId = 999L;

        OrderItemCreateDTO itemDTO = new OrderItemCreateDTO();
        itemDTO.setDishId(dishId);
        itemDTO.setQuantity(2);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setItems(List.of(itemDTO));

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.setIfAbsent(
                    anyString(),
                    anyLong(),
                    any()
            )).thenReturn(true);

            when(redisService.tryLock(
                    anyString(),
                    anyString(),
                    any()
            )).thenReturn(true);

            when(dishMapper.findById(
                    dishId,
                    merchantId
            )).thenReturn(null);

            assertThatThrownBy(() ->
                    orderService.create(dto)
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.DISH_NOT_FOUND,
                            ErrorMessage.DISH_NOT_FOUND
                    )
            );

            verify(redisService)
                    .setIfAbsent(
                            anyString(),
                            anyLong(),
                            any()
                    );

            verify(redisService)
                    .tryLock(
                            anyString(),
                            anyString(),
                            any()
                    );

            verify(dishMapper)
                    .findById(
                            dishId,
                            merchantId
                    );

            verify(orderMapper, never())
                    .insert(any(Order.class));

            verify(orderItemMapper, never())
                    .insert(any(OrderItem.class));

            verify(redisService)
                    .unlock(
                            anyString(),
                            anyString()
                    );
        }
    }

    @Test
    void create_shouldRejectWhenDishIsDisabled() {
        Long userId = 1L;
        Long merchantId = 1L;
        Long dishId = 10L;

        OrderItemCreateDTO itemDTO = new OrderItemCreateDTO();
        itemDTO.setDishId(dishId);
        itemDTO.setQuantity(2);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setItems(List.of(itemDTO));

        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setMerchantId(merchantId);
        dish.setName("Kung Pao Chicken");
        dish.setPrice(new BigDecimal("15.99"));
        dish.setStatus(DishStatus.DISABLED);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.setIfAbsent(
                    anyString(),
                    anyLong(),
                    any()
            )).thenReturn(true);

            when(redisService.tryLock(
                    anyString(),
                    anyString(),
                    any()
            )).thenReturn(true);

            when(dishMapper.findById(
                    dishId,
                    merchantId
            )).thenReturn(dish);

            assertThatThrownBy(() ->
                    orderService.create(dto)
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.DISH_DISABLED,
                            ErrorMessage.DISH_DISABLED
                    )
            );

            verify(dishMapper)
                    .findById(
                            dishId,
                            merchantId
                    );

            verify(orderMapper, never())
                    .insert(any(Order.class));

            verify(orderItemMapper, never())
                    .insert(any(OrderItem.class));

            verify(redisService)
                    .unlock(
                            anyString(),
                            anyString()
                    );
        }
    }

    @Test
    void create_shouldCalculateTotalAmountAndCreateMultipleOrderItems() {
        Long userId = 1L;
        Long merchantId = 1L;

        Long dishId1 = 10L;
        Long dishId2 = 20L;

        OrderItemCreateDTO item1 = new OrderItemCreateDTO();
        item1.setDishId(dishId1);
        item1.setQuantity(2);

        OrderItemCreateDTO item2 = new OrderItemCreateDTO();
        item2.setDishId(dishId2);
        item2.setQuantity(3);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setItems(List.of(item1, item2));

        Dish dish1 = new Dish();
        dish1.setId(dishId1);
        dish1.setMerchantId(merchantId);
        dish1.setName("Kung Pao Chicken");
        dish1.setPrice(new BigDecimal("15.99"));
        dish1.setStatus(DishStatus.ENABLED);

        Dish dish2 = new Dish();
        dish2.setId(dishId2);
        dish2.setMerchantId(merchantId);
        dish2.setName("Fried Rice");
        dish2.setPrice(new BigDecimal("8.50"));
        dish2.setStatus(DishStatus.ENABLED);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.setIfAbsent(
                    anyString(),
                    anyLong(),
                    any()
            )).thenReturn(true);

            when(redisService.tryLock(
                    anyString(),
                    anyString(),
                    any()
            )).thenReturn(true);

            when(dishMapper.findById(dishId1, merchantId))
                    .thenReturn(dish1);

            when(dishMapper.findById(dishId2, merchantId))
                    .thenReturn(dish2);

            when(orderMapper.insert(any(Order.class)))
                    .thenAnswer(invocation -> {
                        Order order = invocation.getArgument(0);
                        order.setId(100L);
                        return 1;
                    });

            when(orderItemMapper.insert(any(OrderItem.class)))
                    .thenReturn(1);

            orderService.create(dto);

            // 15.99 × 2 + 8.50 × 3 = 57.48
            verify(orderMapper)
                    .insert(
                            argThat(order ->
                                    new BigDecimal("57.48")
                                            .compareTo(order.getTotalAmount()) == 0
                                            && merchantId.equals(order.getMerchantId())
                                            && userId.equals(order.getUserId())
                                            && order.getStatus() ==
                                            OrderStatus.PENDING
                            )
                    );

            verify(orderItemMapper, times(2))
                    .insert(any(OrderItem.class));

            verify(orderItemMapper)
                    .insert(
                            argThat(orderItem ->
                                    dishId1.equals(orderItem.getDishId())
                                            && "Kung Pao Chicken".equals(
                                            orderItem.getDishName()
                                    )
                                            && new BigDecimal("15.99")
                                            .compareTo(
                                                    orderItem.getPrice()
                                            ) == 0
                                            && Integer.valueOf(2).equals(
                                            orderItem.getQuantity()
                                    )
                                            && new BigDecimal("31.98")
                                            .compareTo(
                                                    orderItem.getAmount()
                                            ) == 0
                            )
                    );

            verify(orderItemMapper)
                    .insert(
                            argThat(orderItem ->
                                    dishId2.equals(orderItem.getDishId())
                                            && "Fried Rice".equals(
                                            orderItem.getDishName()
                                    )
                                            && new BigDecimal("8.50")
                                            .compareTo(
                                                    orderItem.getPrice()
                                            ) == 0
                                            && Integer.valueOf(3).equals(
                                            orderItem.getQuantity()
                                    )
                                            && new BigDecimal("25.50")
                                            .compareTo(
                                                    orderItem.getAmount()
                                            ) == 0
                            )
                    );

            verify(redisService)
                    .unlock(
                            anyString(),
                            anyString()
                    );
        }
    }

    @Test
    void create_shouldRejectWhenOrderCreationFails() {
        Long userId = 1L;
        Long merchantId = 1L;
        Long dishId = 10L;

        OrderItemCreateDTO itemDTO = new OrderItemCreateDTO();
        itemDTO.setDishId(dishId);
        itemDTO.setQuantity(2);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setItems(List.of(itemDTO));

        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setMerchantId(merchantId);
        dish.setName("Kung Pao Chicken");
        dish.setPrice(new BigDecimal("15.99"));
        dish.setStatus(DishStatus.ENABLED);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.setIfAbsent(
                    anyString(),
                    anyLong(),
                    any()
            )).thenReturn(true);

            when(redisService.tryLock(
                    anyString(),
                    anyString(),
                    any()
            )).thenReturn(true);

            when(dishMapper.findById(
                    dishId,
                    merchantId
            )).thenReturn(dish);

            when(orderMapper.insert(any(Order.class)))
                    .thenReturn(0);

            assertThatThrownBy(() ->
                    orderService.create(dto)
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.ORDER_CREATE_FAILED,
                            ErrorMessage.ORDER_CREATE_FAILED
                    )
            );

            verify(dishMapper)
                    .findById(
                            dishId,
                            merchantId
                    );

            verify(orderMapper)
                    .insert(
                            argThat(order ->
                                    merchantId.equals(order.getMerchantId())
                                            && userId.equals(order.getUserId())
                                            && new BigDecimal("31.98")
                                            .compareTo(order.getTotalAmount()) == 0
                                            && order.getStatus() ==
                                            OrderStatus.PENDING
                            )
                    );

            verify(orderItemMapper, never())
                    .insert(any(OrderItem.class));

            verify(redisService)
                    .unlock(
                            anyString(),
                            anyString()
                    );
        }
    }

    @Test
    void create_shouldRejectWhenOrderItemCreationFails() {
        Long userId = 1L;
        Long merchantId = 1L;
        Long dishId = 10L;

        OrderItemCreateDTO itemDTO = new OrderItemCreateDTO();
        itemDTO.setDishId(dishId);
        itemDTO.setQuantity(2);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setItems(List.of(itemDTO));

        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setMerchantId(merchantId);
        dish.setName("Kung Pao Chicken");
        dish.setPrice(new BigDecimal("15.99"));
        dish.setStatus(DishStatus.ENABLED);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentUserId)
                    .thenReturn(userId);

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            when(redisService.setIfAbsent(
                    anyString(),
                    anyLong(),
                    any()
            )).thenReturn(true);

            when(redisService.tryLock(
                    anyString(),
                    anyString(),
                    any()
            )).thenReturn(true);

            when(dishMapper.findById(
                    dishId,
                    merchantId
            )).thenReturn(dish);

            when(orderMapper.insert(any(Order.class)))
                    .thenAnswer(invocation -> {
                        Order order = invocation.getArgument(0);
                        order.setId(100L);
                        return 1;
                    });

            when(orderItemMapper.insert(any(OrderItem.class)))
                    .thenReturn(0);

            assertThatThrownBy(() ->
                    orderService.create(dto)
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.ORDER_ITEM_CREATE_FAILED,
                            ErrorMessage.ORDER_ITEM_CREATE_FAILED
                    )
            );

            verify(orderMapper)
                    .insert(
                            argThat(order ->
                                    merchantId.equals(order.getMerchantId())
                                            && userId.equals(order.getUserId())
                                            && new BigDecimal("31.98")
                                            .compareTo(order.getTotalAmount()) == 0
                                            && order.getStatus() == OrderStatus.PENDING
                                            && Long.valueOf(100L).equals(
                                            order.getId()
                                    )
                            )
                    );

            verify(orderItemMapper)
                    .insert(
                            argThat(orderItem ->
                                    merchantId.equals(
                                            orderItem.getMerchantId()
                                    )
                                            && Long.valueOf(100L).equals(
                                            orderItem.getOrderId()
                                    )
                                            && dishId.equals(
                                            orderItem.getDishId()
                                    )
                                            && "Kung Pao Chicken".equals(
                                            orderItem.getDishName()
                                    )
                                            && new BigDecimal("15.99")
                                            .compareTo(
                                                    orderItem.getPrice()
                                            ) == 0
                                            && Integer.valueOf(2).equals(
                                            orderItem.getQuantity()
                                    )
                                            && new BigDecimal("31.98")
                                            .compareTo(
                                                    orderItem.getAmount()
                                            ) == 0
                            )
                    );

            verify(redisService)
                    .unlock(
                            anyString(),
                            anyString()
                    );
        }
    }

    // ==================== Page ====================
    @Test
    void page_shouldReturnPagedOrdersSuccessfully() {
        Long merchantId = 1L;

        OrderQueryDTO query = new OrderQueryDTO();
        query.setPage(2);
        query.setPageSize(10);
        query.setStatus(OrderStatus.PENDING);

        Order order1 = new Order();
        order1.setId(1L);
        order1.setMerchantId(merchantId);
        order1.setUserId(100L);
        order1.setOrderNumber("ORDER-001");
        order1.setTotalAmount(new BigDecimal("35.98"));
        order1.setStatus(OrderStatus.PENDING);
        order1.setCreatedAt(
                LocalDateTime.of(2026, 8, 30, 10, 0)
        );

        Order order2 = new Order();
        order2.setId(2L);
        order2.setMerchantId(merchantId);
        order2.setUserId(101L);
        order2.setOrderNumber("ORDER-002");
        order2.setTotalAmount(new BigDecimal("50.00"));
        order2.setStatus(OrderStatus.PENDING);
        order2.setCreatedAt(
                LocalDateTime.of(2026, 8, 30, 9, 30)
        );

        when(orderMapper.findPage(
                OrderStatus.PENDING,
                merchantId,
                10,
                10
        )).thenReturn(List.of(order1, order2));

        when(orderMapper.count(
                OrderStatus.PENDING,
                merchantId
        )).thenReturn(25L);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            PageResult<OrderPageDTO> result =
                    orderService.page(query);

            assertThat(result).isNotNull();
            assertThat(result.getTotal()).isEqualTo(25L);
            assertThat(result.getRecords()).hasSize(2);

            assertThat(result.getRecords().get(0).getId())
                    .isEqualTo(1L);
            assertThat(result.getRecords().get(0).getOrderNumber())
                    .isEqualTo("ORDER-001");
            assertThat(result.getRecords().get(0).getTotalAmount())
                    .isEqualByComparingTo("35.98");
            assertThat(result.getRecords().get(0).getStatus())
                    .isEqualTo(OrderStatus.PENDING);
            assertThat(result.getRecords().get(0).getCreatedAt())
                    .isEqualTo(order1.getCreatedAt());

            assertThat(result.getRecords().get(1).getId())
                    .isEqualTo(2L);
            assertThat(result.getRecords().get(1).getOrderNumber())
                    .isEqualTo("ORDER-002");
            assertThat(result.getRecords().get(1).getTotalAmount())
                    .isEqualByComparingTo("50.00");
            assertThat(result.getRecords().get(1).getStatus())
                    .isEqualTo(OrderStatus.PENDING);

            verify(orderMapper)
                    .findPage(
                            OrderStatus.PENDING,
                            merchantId,
                            10,
                            10
                    );

            verify(orderMapper)
                    .count(
                            OrderStatus.PENDING,
                            merchantId
                    );
        }
    }

    @Test
    void page_shouldReturnAllOrdersWhenStatusIsNull() {
        Long merchantId = 1L;

        OrderQueryDTO query = new OrderQueryDTO();
        query.setPage(1);
        query.setPageSize(10);
        query.setStatus(null);

        Order order1 = new Order();
        order1.setId(1L);
        order1.setMerchantId(merchantId);
        order1.setUserId(100L);
        order1.setOrderNumber("ORDER-001");
        order1.setTotalAmount(new BigDecimal("35.98"));
        order1.setStatus(OrderStatus.PENDING);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setMerchantId(merchantId);
        order2.setUserId(101L);
        order2.setOrderNumber("ORDER-002");
        order2.setTotalAmount(new BigDecimal("50.00"));
        order2.setStatus(OrderStatus.COMPLETED);

        when(orderMapper.findPage(
                null,
                merchantId,
                0,
                10
        )).thenReturn(List.of(order1, order2));

        when(orderMapper.count(
                null,
                merchantId
        )).thenReturn(2L);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            PageResult<OrderPageDTO> result =
                    orderService.page(query);

            assertThat(result).isNotNull();
            assertThat(result.getTotal()).isEqualTo(2L);
            assertThat(result.getRecords()).hasSize(2);

            assertThat(result.getRecords().get(0).getId())
                    .isEqualTo(1L);
            assertThat(result.getRecords().get(0).getOrderNumber())
                    .isEqualTo("ORDER-001");
            assertThat(result.getRecords().get(0).getStatus())
                    .isEqualTo(OrderStatus.PENDING);

            assertThat(result.getRecords().get(1).getId())
                    .isEqualTo(2L);
            assertThat(result.getRecords().get(1).getOrderNumber())
                    .isEqualTo("ORDER-002");
            assertThat(result.getRecords().get(1).getStatus())
                    .isEqualTo(OrderStatus.COMPLETED);

            verify(orderMapper)
                    .findPage(
                            null,
                            merchantId,
                            0,
                            10
                    );

            verify(orderMapper)
                    .count(
                            null,
                            merchantId
                    );
        }
    }

    @Test
    void page_shouldReturnEmptyPageWhenNoOrdersFound() {
        Long merchantId = 1L;

        OrderQueryDTO query = new OrderQueryDTO();
        query.setPage(1);
        query.setPageSize(10);
        query.setStatus(OrderStatus.PENDING);

        when(orderMapper.findPage(
                OrderStatus.PENDING,
                merchantId,
                0,
                10
        )).thenReturn(List.of());

        when(orderMapper.count(
                OrderStatus.PENDING,
                merchantId
        )).thenReturn(0L);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            PageResult<OrderPageDTO> result =
                    orderService.page(query);

            assertThat(result).isNotNull();
            assertThat(result.getTotal()).isZero();
            assertThat(result.getRecords()).isEmpty();

            verify(orderMapper)
                    .findPage(
                            OrderStatus.PENDING,
                            merchantId,
                            0,
                            10
                    );

            verify(orderMapper)
                    .count(
                            OrderStatus.PENDING,
                            merchantId
                    );
        }
    }

    // ==================== Get By ID ====================
    @Test
    void getById_shouldReturnOrderDetailSuccessfully() {
        Long merchantId = 1L;
        Long orderId = 100L;

        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchantId);
        order.setUserId(10L);
        order.setOrderNumber("ORDER-001");
        order.setTotalAmount(new BigDecimal("57.48"));
        order.setStatus(OrderStatus.COMPLETED);

        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setMerchantId(merchantId);
        item1.setOrderId(orderId);
        item1.setDishId(10L);
        item1.setDishName("Kung Pao Chicken");
        item1.setPrice(new BigDecimal("15.99"));
        item1.setQuantity(2);
        item1.setAmount(new BigDecimal("31.98"));

        OrderItem item2 = new OrderItem();
        item2.setId(2L);
        item2.setMerchantId(merchantId);
        item2.setOrderId(orderId);
        item2.setDishId(20L);
        item2.setDishName("Fried Rice");
        item2.setPrice(new BigDecimal("8.50"));
        item2.setQuantity(3);
        item2.setAmount(new BigDecimal("25.50"));

        when(orderMapper.findById(
                orderId,
                merchantId
        )).thenReturn(order);

        when(orderItemMapper.findByOrderId(
                orderId,
                merchantId
        )).thenReturn(List.of(item1, item2));

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            OrderDetailDTO result =
                    orderService.getById(orderId);

            assertThat(result).isNotNull();

            assertThat(result.getId())
                    .isEqualTo(orderId);
            assertThat(result.getOrderNumber())
                    .isEqualTo("ORDER-001");
            assertThat(result.getTotalAmount())
                    .isEqualByComparingTo("57.48");
            assertThat(result.getStatus())
                    .isEqualTo(OrderStatus.COMPLETED);

            assertThat(result.getItems())
                    .hasSize(2);

            assertThat(result.getItems().get(0).getDishId())
                    .isEqualTo(10L);
            assertThat(result.getItems().get(0).getDishName())
                    .isEqualTo("Kung Pao Chicken");
            assertThat(result.getItems().get(0).getPrice())
                    .isEqualByComparingTo("15.99");
            assertThat(result.getItems().get(0).getQuantity())
                    .isEqualTo(2);
            assertThat(result.getItems().get(0).getAmount())
                    .isEqualByComparingTo("31.98");

            assertThat(result.getItems().get(1).getDishId())
                    .isEqualTo(20L);
            assertThat(result.getItems().get(1).getDishName())
                    .isEqualTo("Fried Rice");
            assertThat(result.getItems().get(1).getPrice())
                    .isEqualByComparingTo("8.50");
            assertThat(result.getItems().get(1).getQuantity())
                    .isEqualTo(3);
            assertThat(result.getItems().get(1).getAmount())
                    .isEqualByComparingTo("25.50");

            verify(orderMapper)
                    .findById(
                            orderId,
                            merchantId
                    );

            verify(orderItemMapper)
                    .findByOrderId(
                            orderId,
                            merchantId
                    );
        }
    }

    @Test
    void getById_shouldRejectWhenOrderNotFound() {
        Long merchantId = 1L;
        Long orderId = 999L;

        when(orderMapper.findById(
                orderId,
                merchantId
        )).thenReturn(null);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            assertThatThrownBy(() ->
                    orderService.getById(orderId)
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.ORDER_NOT_FOUND,
                            ErrorMessage.ORDER_NOT_FOUND
                    )
            );

            verify(orderMapper)
                    .findById(
                            orderId,
                            merchantId
                    );

            verify(orderItemMapper, never())
                    .findByOrderId(
                            anyLong(),
                            anyLong()
                    );
        }
    }

    // ==================== Update ====================
    @Test
    void updateStatus_shouldUpdateOrderStatusSuccessfully() {
        Long merchantId = 1L;
        Long orderId = 100L;

        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchantId);
        order.setUserId(10L);
        order.setOrderNumber("ORDER-001");
        order.setTotalAmount(new BigDecimal("35.98"));
        order.setStatus(OrderStatus.PENDING);

        when(orderMapper.findById(
                orderId,
                merchantId
        )).thenReturn(order);

        when(orderMapper.updateStatus(
                orderId,
                merchantId,
                OrderStatus.PAID
        )).thenReturn(1);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            orderService.updateStatus(
                    orderId,
                    OrderStatus.PAID
            );

            verify(orderMapper)
                    .findById(
                            orderId,
                            merchantId
                    );

            verify(orderMapper)
                    .updateStatus(
                            orderId,
                            merchantId,
                            OrderStatus.PAID
                    );
        }
    }

    @Test
    void updateStatus_shouldRejectWhenStatusIsInvalid() {
        Long merchantId = 1L;
        Long orderId = 100L;
        Integer invalidStatus = 99;

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            assertThatThrownBy(() ->
                    orderService.updateStatus(
                            orderId,
                            invalidStatus
                    )
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.ORDER_STATUS_INVALID,
                            ErrorMessage.ORDER_STATUS_INVALID
                    )
            );

            verify(orderMapper, never())
                    .findById(
                            anyLong(),
                            anyLong()
                    );

            verify(orderMapper, never())
                    .updateStatus(
                            anyLong(),
                            anyLong(),
                            anyInt()
                    );
        }
    }

    @Test
    void updateStatus_shouldRejectWhenOrderNotFound() {
        Long merchantId = 1L;
        Long orderId = 999L;

        when(orderMapper.findById(
                orderId,
                merchantId
        )).thenReturn(null);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            assertThatThrownBy(() ->
                    orderService.updateStatus(
                            orderId,
                            OrderStatus.PAID
                    )
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.ORDER_NOT_FOUND,
                            ErrorMessage.ORDER_NOT_FOUND
                    )
            );

            verify(orderMapper)
                    .findById(
                            orderId,
                            merchantId
                    );

            verify(orderMapper, never())
                    .updateStatus(
                            anyLong(),
                            anyLong(),
                            anyInt()
                    );
        }
    }

    @Test
    void updateStatus_shouldRejectWhenStatusTransitionIsInvalid() {
        Long merchantId = 1L;
        Long orderId = 100L;

        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchantId);
        order.setUserId(10L);
        order.setOrderNumber("ORDER-001");
        order.setTotalAmount(new BigDecimal("35.98"));
        order.setStatus(OrderStatus.PENDING);

        when(orderMapper.findById(
                orderId,
                merchantId
        )).thenReturn(order);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            assertThatThrownBy(() ->
                    orderService.updateStatus(
                            orderId,
                            OrderStatus.COMPLETED
                    )
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.ORDER_STATUS_TRANSITION_INVALID,
                            ErrorMessage.ORDER_STATUS_TRANSITION_INVALID
                    )
            );

            verify(orderMapper)
                    .findById(
                            orderId,
                            merchantId
                    );

            verify(orderMapper, never())
                    .updateStatus(
                            anyLong(),
                            anyLong(),
                            anyInt()
                    );
        }
    }

    @Test
    void updateStatus_shouldRejectWhenOrderStatusUpdateFails() {
        Long merchantId = 1L;
        Long orderId = 100L;

        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchantId);
        order.setUserId(10L);
        order.setOrderNumber("ORDER-001");
        order.setTotalAmount(new BigDecimal("35.98"));
        order.setStatus(OrderStatus.PENDING);

        when(orderMapper.findById(
                orderId,
                merchantId
        )).thenReturn(order);

        when(orderMapper.updateStatus(
                orderId,
                merchantId,
                OrderStatus.PAID
        )).thenReturn(0);

        try (MockedStatic<AuthContext> authContextMock =
                     mockStatic(AuthContext.class)) {

            authContextMock
                    .when(AuthContext::getCurrentMerchantId)
                    .thenReturn(merchantId);

            assertThatThrownBy(() ->
                    orderService.updateStatus(
                            orderId,
                            OrderStatus.PAID
                    )
            ).satisfies(exception ->
                    assertBusinessException(
                            exception,
                            ErrorCode.ORDER_UPDATE_FAILED,
                            ErrorMessage.ORDER_UPDATE_FAILED
                    )
            );

            verify(orderMapper)
                    .findById(
                            orderId,
                            merchantId
                    );

            verify(orderMapper)
                    .updateStatus(
                            orderId,
                            merchantId,
                            OrderStatus.PAID
                    );
        }
    }

    // ==================== Auto Cancel Expired Orders ====================
    @Test
    void autoCancelExpiredOrder_shouldCancelExpiredOrderSuccessfully() {
        Long orderId = 100L;

        when(orderMapper.updateStatusById(
                orderId,
                OrderStatus.CANCELLED
        )).thenReturn(1);

        orderService.autoCancelExpiredOrder(orderId);

        verify(orderMapper)
                .updateStatusById(
                        orderId,
                        OrderStatus.CANCELLED
                );
    }

    @Test
    void autoCancelExpiredOrder_shouldRejectWhenUpdateFails() {
        Long orderId = 100L;

        when(orderMapper.updateStatusById(
                orderId,
                OrderStatus.CANCELLED
        )).thenReturn(0);

        assertThatThrownBy(() ->
                orderService.autoCancelExpiredOrder(orderId)
        ).satisfies(exception ->
                assertBusinessException(
                        exception,
                        ErrorCode.ORDER_UPDATE_FAILED,
                        ErrorMessage.ORDER_UPDATE_FAILED
                )
        );

        verify(orderMapper)
                .updateStatusById(
                        orderId,
                        OrderStatus.CANCELLED
                );
    }

    private void assertBusinessException(
            Throwable exception,
            Integer expectedCode,
            String expectedMessage
    ) {
        assertThat(exception)
                .isInstanceOf(BusinessException.class);

        BusinessException businessException =
                (BusinessException) exception;

        assertThat(businessException.getCode())
                .isEqualTo(expectedCode);

        assertThat(businessException.getMessage())
                .isEqualTo(expectedMessage);
    }

}