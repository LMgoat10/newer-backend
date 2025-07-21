package com.newer.jay.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("ticket_order")
public class TicketOrder {
    @TableId
    private String id; // 订单ID，手动生成，同时作为订单号
    
    private Integer userId;        // 改为Integer以匹配user表的int类型
    private Long attractionId;
    private LocalDate visitDate;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    
    // 联系人信息
    private String contactName;
    private String contactPhone;
    private String contactIdcard;
    private String address;
    
    // 订单状态
    private OrderStatus status;
    
    // 支付信息
    private PayMethod payMethod;
    private LocalDateTime payTime;
    private BigDecimal payAmount;
    
    // 退款信息
    private String refundReason;
    private LocalDateTime refundTime;
    private BigDecimal refundAmount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum OrderStatus {
        PAID,           // 已支付，待放票
        TICKETED,       // 已放票，订单完成
        REFUND_REQUEST, // 退票申请中
        REFUNDED,       // 已退款
        CANCELLED       // 取消（超时未付款或用户取消）
    }
    
    public enum PayMethod {
        BALANCE, ALIPAY, WECHAT
    }
    
    // 构造函数用于创建新订单
    public TicketOrder(Integer userId, Long attractionId, String orderId,
                      Integer quantity, BigDecimal unitPrice, BigDecimal totalAmount,
                      LocalDate visitDate, String contactName, String contactPhone) {
        this.id = orderId;
        this.userId = userId;
        this.attractionId = attractionId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.payAmount = totalAmount; // 默认支付金额为总金额
        this.visitDate = visitDate;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.status = OrderStatus.PAID;  // 默认状态改为PAID
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 设置支付时间的setter
    public void setPaidAt(LocalDateTime paidAt) {
        this.payTime = paidAt;
    }
    
    // 获取支付时间的getter
    public LocalDateTime getPaidAt() {
        return this.payTime;
    }
    
    // 获取订单号（使用ID作为订单号）
    public String getOrderNumber() {
        return this.id;
    }
}
