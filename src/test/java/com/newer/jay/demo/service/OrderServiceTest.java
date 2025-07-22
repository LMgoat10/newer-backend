package com.newer.jay.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.OrderCreateRequestDTO;
import com.newer.jay.demo.dto.OrderResponseDTO;
import com.newer.jay.demo.dto.PageResponse;
import com.newer.jay.demo.entity.Attraction;
import com.newer.jay.demo.entity.TicketOrder;
import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.mapper.AttractionMapper;
import com.newer.jay.demo.mapper.TicketOrderMapper;
import com.newer.jay.demo.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * OrderService服务类测试 - 基于JUnit5和Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService服务测试")
public class OrderServiceTest {

    @Mock
    private TicketOrderMapper ticketOrderMapper;

    @Mock
    private AttractionMapper attractionMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private OrderService orderService;

    private User mockUser;
    private Attraction mockAttraction;
    private TicketOrder mockOrder;
    private OrderCreateRequestDTO mockCreateRequest;

    @BeforeEach
    void setUp() {
        // 创建模拟用户
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setName("测试用户");
        mockUser.setEmail("test@example.com");
        mockUser.setBalance(1000.0);

        // 创建模拟景点
        mockAttraction = new Attraction();
        mockAttraction.setId(1L);
        mockAttraction.setName("测试景点");
        mockAttraction.setPrice(new BigDecimal("100.00"));
        mockAttraction.setTotalTickets(1000);
        mockAttraction.setAvailableTickets(800);
        mockAttraction.setStatus(Attraction.AttractionStatus.ACTIVE);

        // 创建模拟订单
        mockOrder = new TicketOrder();
        mockOrder.setId("ORDER123456");
        mockOrder.setUserId(1);
        mockOrder.setAttractionId(1L);
        mockOrder.setQuantity(2);
        mockOrder.setUnitPrice(new BigDecimal("100.00"));
        mockOrder.setTotalAmount(new BigDecimal("200.00"));
        mockOrder.setVisitDate(LocalDate.now().plusDays(7));
        mockOrder.setContactName("张三");
        mockOrder.setContactPhone("13800138000");
        mockOrder.setStatus(TicketOrder.OrderStatus.PAID);
        mockOrder.setCreatedAt(LocalDateTime.now());
        mockOrder.setUpdatedAt(LocalDateTime.now());

        // 创建模拟创建订单请求
        mockCreateRequest = new OrderCreateRequestDTO();
        mockCreateRequest.setUserId(1);
        mockCreateRequest.setAttractionId(1L);
        mockCreateRequest.setQuantity(2);
        mockCreateRequest.setVisitDate(LocalDate.now().plusDays(7));
        mockCreateRequest.setContactName("张三");
        mockCreateRequest.setContactPhone("13800138000");
    }

    @Test
    @DisplayName("测试获取用户订单列表 - 成功")
    void testGetUserOrdersSuccess() {
        // 准备测试数据
        List<TicketOrder> mockOrders = Arrays.asList(mockOrder);
        Page<TicketOrder> mockPage = new Page<>(1, 10);
        mockPage.setRecords(mockOrders);
        mockPage.setTotal(1);

        // 模拟mapper行为
        when(ticketOrderMapper.selectUserOrdersPage(any(), eq(1), eq(null), eq(null), eq(null))).thenReturn(mockPage);
        when(attractionMapper.selectById(1L)).thenReturn(mockAttraction);

        // 执行测试
        PageResponse<OrderResponseDTO> result = orderService.getUserOrders(1, 1, 10, null);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        OrderResponseDTO dto = result.getContent().get(0);
        assertEquals(Integer.valueOf(1), dto.getUserId());
        assertEquals(Long.valueOf(1L), dto.getAttractionId());

        // 验证mapper调用
        verify(ticketOrderMapper, times(1)).selectUserOrdersPage(any(), eq(1), eq(null), eq(null), eq(null));
        verify(attractionMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试获取订单详情 - 成功")
    void testGetOrderDetailsSuccess() {
        // 模拟mapper行为
        when(ticketOrderMapper.selectOne(any())).thenReturn(mockOrder);
        when(attractionMapper.selectById(1L)).thenReturn(mockAttraction);

        // 执行测试
        OrderResponseDTO result = orderService.getOrderDetails("ORDER123456");

        // 验证结果
        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getUserId());
        assertEquals(Long.valueOf(1L), result.getAttractionId());

        // 验证mapper调用
        verify(ticketOrderMapper, times(1)).selectOne(any());
        verify(attractionMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试获取订单详情 - 订单不存在")
    void testGetOrderDetailsNotFound() {
        // 模拟订单不存在
        when(ticketOrderMapper.selectOne(any())).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderDetails("NONEXISTENT");
        });

        assertTrue(exception.getMessage().contains("订单不存在"));

        // 验证mapper调用
        verify(ticketOrderMapper, times(1)).selectOne(any());
        verify(attractionMapper, never()).selectById(any());
    }

    @Test
    @DisplayName("测试取消订单 - 成功")
    void testCancelOrderSuccess() {
        // 模拟mapper行为
        when(ticketOrderMapper.selectOne(any())).thenReturn(mockOrder);
        when(ticketOrderMapper.updateById(any(TicketOrder.class))).thenReturn(1);
        when(attractionMapper.selectById(1L)).thenReturn(mockAttraction);
        when(attractionMapper.updateById(any(Attraction.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> {
            orderService.cancelOrder("ORDER123456", 1);
        });

        // 验证mapper调用
        verify(ticketOrderMapper, times(1)).selectOne(any());
        verify(ticketOrderMapper, times(1)).updateById(any(TicketOrder.class));
        verify(attractionMapper, times(1)).selectById(1L);
        verify(attractionMapper, times(1)).updateById(any(Attraction.class));
    }

    @Test
    @DisplayName("测试取消订单 - 用户不匹配")
    void testCancelOrderUserMismatch() {
        // 模拟订单不存在（因为用户ID不匹配）
        when(ticketOrderMapper.selectOne(any())).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder("ORDER123456", 999);
        });

        assertTrue(exception.getMessage().contains("订单不存在"));

        // 验证mapper调用
        verify(ticketOrderMapper, times(1)).selectOne(any());
        verify(ticketOrderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("测试取消订单 - 订单状态不允许取消")
    void testCancelOrderInvalidStatus() {
        // 设置订单状态为已放票，不允许取消
        mockOrder.setStatus(TicketOrder.OrderStatus.TICKETED);
        
        // 模拟mapper行为
        when(ticketOrderMapper.selectOne(any())).thenReturn(mockOrder);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder("ORDER123456", 1);
        });

        assertTrue(exception.getMessage().contains("只能取消已支付的订单"));

        // 验证mapper调用
        verify(ticketOrderMapper, times(1)).selectOne(any());
        verify(ticketOrderMapper, never()).updateById(any());
    }
}
