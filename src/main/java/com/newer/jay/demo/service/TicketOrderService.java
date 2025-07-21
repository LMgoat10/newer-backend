package com.newer.jay.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.newer.jay.demo.entity.TicketOrder;
import com.newer.jay.demo.mapper.TicketOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketOrderService {
    
    @Autowired
    private TicketOrderMapper ticketOrderMapper;
    
    /**
     * 创建订单
     */
    @Transactional
    public Map<String, Object> createOrder(Integer userId, Long attractionId, LocalDate visitDate,
                                         Integer quantity, BigDecimal unitPrice, String contactName,
                                         String contactPhone, String contactIdcard, String address,
                                         TicketOrder.PayMethod payMethod, BigDecimal payAmount) {
        try {
            // 生成订单ID
            String orderId = generateOrderId();
            
            // 计算总金额
            BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(quantity));
            
            // 创建订单对象
            TicketOrder order = new TicketOrder();
            order.setId(orderId);
            order.setUserId(userId);
            order.setAttractionId(attractionId);
            order.setVisitDate(visitDate);
            order.setQuantity(quantity);
            order.setUnitPrice(unitPrice);
            order.setTotalAmount(totalAmount);
            order.setContactName(contactName);
            order.setContactPhone(contactPhone);
            order.setContactIdcard(contactIdcard);
            order.setAddress(address);
            order.setPayMethod(payMethod);
            order.setPayAmount(payAmount);
            order.setPayTime(LocalDateTime.now());  // 设置支付时间
            order.setStatus(TicketOrder.OrderStatus.PAID);  // 默认状态为PAID
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            
            ticketOrderMapper.insert(order);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", 0);
            result.put("message", "订单创建成功");
            result.put("orderId", orderId);
            result.put("order", order);
            
            return result;
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", 1);
            result.put("message", "订单创建失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 根据用户ID查询订单列表
     */
    public List<TicketOrder> getUserOrders(Integer userId) {
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TicketOrder::getUserId, userId)
               .orderByDesc(TicketOrder::getCreatedAt);
        
        return ticketOrderMapper.selectList(wrapper);
    }
    
    /**
     * 根据订单ID查询订单
     */
    public TicketOrder getOrderById(String orderId) {
        return ticketOrderMapper.selectById(orderId);
    }
    
    /**
     * 更新订单状态
     */
    @Transactional
    public void updateOrderStatus(String orderId, TicketOrder.OrderStatus status) {
        TicketOrder order = ticketOrderMapper.selectById(orderId);
        if (order != null) {
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            ticketOrderMapper.updateById(order);
        }
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public Map<String, Object> cancelOrder(String orderId, String reason) {
        try {
            TicketOrder order = ticketOrderMapper.selectById(orderId);
            if (order == null) {
                return Map.of("status", 1, "message", "订单不存在");
            }
            
            // 只能取消已支付状态的订单
            if (order.getStatus() != TicketOrder.OrderStatus.PAID) {
                return Map.of("status", 1, "message", "只能取消已支付状态的订单");
            }
            
            // 更新订单状态为已取消
            order.setStatus(TicketOrder.OrderStatus.CANCELLED);
            order.setRefundReason(reason);
            order.setUpdatedAt(LocalDateTime.now());
            
            ticketOrderMapper.updateById(order);
            
            return Map.of("status", 0, "message", "订单取消成功");
            
        } catch (Exception e) {
            return Map.of("status", 1, "message", "取消订单失败: " + e.getMessage());
        }
    }
    
    /**
     * 申请退款
     */
    @Transactional
    public Map<String, Object> applyRefund(String orderId, String reason) {
        try {
            TicketOrder order = ticketOrderMapper.selectById(orderId);
            if (order == null) {
                return Map.of("status", 1, "message", "订单不存在");
            }
            
            // 只能对已放票的订单申请退款
            if (order.getStatus() != TicketOrder.OrderStatus.TICKETED) {
                return Map.of("status", 1, "message", "只能对已放票的订单申请退款");
            }
            
            // 更新订单状态为退款申请中
            order.setStatus(TicketOrder.OrderStatus.REFUND_REQUEST);
            order.setRefundReason(reason);
            order.setUpdatedAt(LocalDateTime.now());
            
            ticketOrderMapper.updateById(order);
            
            return Map.of("status", 0, "message", "退款申请提交成功");
            
        } catch (Exception e) {
            return Map.of("status", 1, "message", "申请退款失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成订单ID
     */
    private String generateOrderId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.format("%03d", (int) (Math.random() * 1000));
        return "ORDER_" + timestamp + "_" + randomSuffix;
    }
}
