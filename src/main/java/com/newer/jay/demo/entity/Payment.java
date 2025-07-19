package com.newer.jay.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("payment")
public class Payment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String paymentId;
    private String orderId;
    private Long userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    
    public enum PaymentMethod {
        BALANCE, ALIPAY, WECHAT
    }
    
    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED, REFUNDED
    }
    
    public Payment(String paymentId, String orderId, Long userId, BigDecimal amount, PaymentMethod paymentMethod) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
