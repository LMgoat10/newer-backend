package com.newer.jay.demo.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {
    
    // 模拟订单数据存储（实际应用中应该从数据库获取）
    private Map<String, OrderInfo> orders = new HashMap<>();
    
    static class OrderInfo {
        private String orderId;
        private BigDecimal amount;
        private String status;
        
        public OrderInfo(String orderId, BigDecimal amount, String status) {
            this.orderId = orderId;
            this.amount = amount;
            this.status = status;
        }
        
        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    public OrderService() {
        // 初始化一些测试数据
        orders.put("ORDER_20250718_001", new OrderInfo("ORDER_20250718_001", new BigDecimal("240.00"), "PENDING"));
        orders.put("ORDER_20250719_001", new OrderInfo("ORDER_20250719_001", new BigDecimal("150.00"), "PENDING"));
    }
    
    /**
     * 获取订单金额
     */
    public BigDecimal getOrderAmount(String orderId) {
        OrderInfo order = orders.get(orderId);
        return order != null ? order.getAmount() : null;
    }
    
    /**
     * 检查订单是否已支付
     */
    public boolean isOrderPaid(String orderId) {
        OrderInfo order = orders.get(orderId);
        return order != null && "PAID".equals(order.getStatus());
    }
    
    /**
     * 更新订单状态
     */
    public void updateOrderStatus(String orderId, String status) {
        OrderInfo order = orders.get(orderId);
        if (order != null) {
            order.setStatus(status);
        }
    }
    
    /**
     * 获取订单信息
     */
    public OrderInfo getOrderInfo(String orderId) {
        return orders.get(orderId);
    }
}
