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
    private String id; // 订单ID，手动生成
    
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
    private PaymentMethod payMethod;
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
    
    public enum PaymentMethod {
        BALANCE, ALIPAY, WECHAT
    }
    
    // 构造函数用于创建新订单
    public TicketOrder(String orderId, Integer userId, Long attractionId, 
                      LocalDate visitDate, Integer quantity, BigDecimal unitPrice,
                      String contactName, String contactPhone, String contactIdcard, String address,
                      PaymentMethod payMethod, BigDecimal payAmount) {
        this.id = orderId;
        this.userId = userId;
        this.attractionId = attractionId;
        this.visitDate = visitDate;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactIdcard = contactIdcard;
        this.address = address;
        this.status = OrderStatus.PAID;
        this.payMethod = payMethod;
        this.payTime = LocalDateTime.now();
        this.payAmount = payAmount;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
