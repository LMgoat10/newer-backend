package com.newer.jay.demo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * TicketOrder实体类测试 - 基于JUnit5
 */
@DisplayName("TicketOrder实体类测试")
public class TicketOrderTest {

    private TicketOrder ticketOrder;

    @BeforeEach
    void setUp() {
        ticketOrder = new TicketOrder();
    }

    @Test
    @DisplayName("测试TicketOrder构造函数")
    void testTicketOrderConstructors() {
        // 测试无参构造函数
        TicketOrder order1 = new TicketOrder();
        assertNotNull(order1);

        // 测试全参构造函数
        LocalDate visitDate = LocalDate.now().plusDays(7);
        LocalDateTime now = LocalDateTime.now();
        BigDecimal unitPrice = new BigDecimal("100.00");
        BigDecimal totalAmount = new BigDecimal("200.00");
        
        TicketOrder order2 = new TicketOrder("ORDER123", 1, 1L, visitDate, 2, 
                unitPrice, totalAmount, "张三", "13800138000", "110101199001011234",
                "北京市朝阳区", TicketOrder.OrderStatus.PAID, TicketOrder.PayMethod.WECHAT,
                now, totalAmount, null, null, null, now, now);
        
        assertEquals("ORDER123", order2.getId());
        assertEquals(Integer.valueOf(1), order2.getUserId());
        assertEquals(Long.valueOf(1L), order2.getAttractionId());
        assertEquals(visitDate, order2.getVisitDate());
        assertEquals(Integer.valueOf(2), order2.getQuantity());
        assertEquals(unitPrice, order2.getUnitPrice());
        assertEquals(totalAmount, order2.getTotalAmount());
        assertEquals("张三", order2.getContactName());
        assertEquals("13800138000", order2.getContactPhone());
        assertEquals(TicketOrder.OrderStatus.PAID, order2.getStatus());
    }

    @Test
    @DisplayName("测试TicketOrder基本字段设置")
    void testTicketOrderBasicFields() {
        ticketOrder.setId("TEST_ORDER_001");
        ticketOrder.setUserId(100);
        ticketOrder.setAttractionId(200L);
        
        LocalDate visitDate = LocalDate.of(2024, 12, 25);
        ticketOrder.setVisitDate(visitDate);
        ticketOrder.setQuantity(3);
        
        BigDecimal unitPrice = new BigDecimal("88.50");
        BigDecimal totalAmount = new BigDecimal("265.50");
        ticketOrder.setUnitPrice(unitPrice);
        ticketOrder.setTotalAmount(totalAmount);

        assertEquals("TEST_ORDER_001", ticketOrder.getId());
        assertEquals(Integer.valueOf(100), ticketOrder.getUserId());
        assertEquals(Long.valueOf(200L), ticketOrder.getAttractionId());
        assertEquals(visitDate, ticketOrder.getVisitDate());
        assertEquals(Integer.valueOf(3), ticketOrder.getQuantity());
        assertEquals(unitPrice, ticketOrder.getUnitPrice());
        assertEquals(totalAmount, ticketOrder.getTotalAmount());
    }

    @Test
    @DisplayName("测试TicketOrder联系人信息")
    void testTicketOrderContactInfo() {
        ticketOrder.setContactName("李四");
        ticketOrder.setContactPhone("13900139000");
        ticketOrder.setContactIdcard("110101199002021234");
        ticketOrder.setAddress("上海市浦东新区陆家嘴金融贸易区");

        assertEquals("李四", ticketOrder.getContactName());
        assertEquals("13900139000", ticketOrder.getContactPhone());
        assertEquals("110101199002021234", ticketOrder.getContactIdcard());
        assertEquals("上海市浦东新区陆家嘴金融贸易区", ticketOrder.getAddress());
    }

    @Test
    @DisplayName("测试TicketOrder订单状态枚举")
    void testTicketOrderStatus() {
        // 测试所有订单状态
        ticketOrder.setStatus(TicketOrder.OrderStatus.PAID);
        assertEquals(TicketOrder.OrderStatus.PAID, ticketOrder.getStatus());
        
        ticketOrder.setStatus(TicketOrder.OrderStatus.TICKETED);
        assertEquals(TicketOrder.OrderStatus.TICKETED, ticketOrder.getStatus());
        
        ticketOrder.setStatus(TicketOrder.OrderStatus.REFUND_REQUEST);
        assertEquals(TicketOrder.OrderStatus.REFUND_REQUEST, ticketOrder.getStatus());
        
        ticketOrder.setStatus(TicketOrder.OrderStatus.CANCELLED);
        assertEquals(TicketOrder.OrderStatus.CANCELLED, ticketOrder.getStatus());
        
        ticketOrder.setStatus(TicketOrder.OrderStatus.REFUNDED);
        assertEquals(TicketOrder.OrderStatus.REFUNDED, ticketOrder.getStatus());
    }

    @Test
    @DisplayName("测试TicketOrder支付方式枚举")
    void testTicketOrderPayMethod() {
        // 测试所有支付方式
        ticketOrder.setPayMethod(TicketOrder.PayMethod.WECHAT);
        assertEquals(TicketOrder.PayMethod.WECHAT, ticketOrder.getPayMethod());
        
        ticketOrder.setPayMethod(TicketOrder.PayMethod.ALIPAY);
        assertEquals(TicketOrder.PayMethod.ALIPAY, ticketOrder.getPayMethod());
        
        ticketOrder.setPayMethod(TicketOrder.PayMethod.BALANCE);
        assertEquals(TicketOrder.PayMethod.BALANCE, ticketOrder.getPayMethod());
    }

    @Test
    @DisplayName("测试TicketOrder支付信息")
    void testTicketOrderPaymentInfo() {
        LocalDateTime payTime = LocalDateTime.now();
        BigDecimal payAmount = new BigDecimal("299.99");
        
        ticketOrder.setPayMethod(TicketOrder.PayMethod.ALIPAY);
        ticketOrder.setPayTime(payTime);
        ticketOrder.setPayAmount(payAmount);

        assertEquals(TicketOrder.PayMethod.ALIPAY, ticketOrder.getPayMethod());
        assertEquals(payTime, ticketOrder.getPayTime());
        assertEquals(payAmount, ticketOrder.getPayAmount());
    }

    @Test
    @DisplayName("测试TicketOrder退款信息")
    void testTicketOrderRefundInfo() {
        String refundReason = "行程取消";
        LocalDateTime refundTime = LocalDateTime.now();
        BigDecimal refundAmount = new BigDecimal("150.00");
        
        ticketOrder.setRefundReason(refundReason);
        ticketOrder.setRefundTime(refundTime);
        ticketOrder.setRefundAmount(refundAmount);

        assertEquals(refundReason, ticketOrder.getRefundReason());
        assertEquals(refundTime, ticketOrder.getRefundTime());
        assertEquals(refundAmount, ticketOrder.getRefundAmount());
    }

    @Test
    @DisplayName("测试TicketOrder时间戳")
    void testTicketOrderTimestamps() {
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        ticketOrder.setCreatedAt(createdAt);
        ticketOrder.setUpdatedAt(updatedAt);

        assertEquals(createdAt, ticketOrder.getCreatedAt());
        assertEquals(updatedAt, ticketOrder.getUpdatedAt());
    }

    @Test
    @DisplayName("测试TicketOrder空值处理")
    void testTicketOrderNullValues() {
        ticketOrder.setContactName(null);
        ticketOrder.setContactPhone(null);
        ticketOrder.setRefundReason(null);
        ticketOrder.setPayMethod(null);
        
        assertNull(ticketOrder.getContactName());
        assertNull(ticketOrder.getContactPhone());
        assertNull(ticketOrder.getRefundReason());
        assertNull(ticketOrder.getPayMethod());
    }

    @Test
    @DisplayName("测试TicketOrder边界值")
    void testTicketOrderBoundaryValues() {
        // 测试数量边界
        ticketOrder.setQuantity(1);
        assertEquals(Integer.valueOf(1), ticketOrder.getQuantity());
        
        ticketOrder.setQuantity(0);
        assertEquals(Integer.valueOf(0), ticketOrder.getQuantity());

        // 测试金额边界
        ticketOrder.setUnitPrice(new BigDecimal("0.01"));
        assertEquals(new BigDecimal("0.01"), ticketOrder.getUnitPrice());
        
        ticketOrder.setTotalAmount(new BigDecimal("0.00"));
        assertEquals(new BigDecimal("0.00"), ticketOrder.getTotalAmount());
    }

    @Test
    @DisplayName("测试TicketOrder的toString方法")
    void testTicketOrderToString() {
        ticketOrder.setId("TEST123");
        ticketOrder.setContactName("测试用户");
        ticketOrder.setStatus(TicketOrder.OrderStatus.PAID);
        
        String orderString = ticketOrder.toString();
        assertNotNull(orderString);
        assertTrue(orderString.contains("TEST123"));
    }
}
