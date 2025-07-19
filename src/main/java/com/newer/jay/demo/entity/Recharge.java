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
@TableName("recharge")
public class Recharge {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String rechargeId;
    private Long userId;
    private BigDecimal amount;
    private Payment.PaymentMethod paymentMethod;
    private RechargeStatus status;
    private LocalDateTime createdAt;
    
    public enum RechargeStatus {
        PENDING, SUCCESS, FAILED
    }
    
    public Recharge(String rechargeId, Long userId, BigDecimal amount, Payment.PaymentMethod paymentMethod) {
        this.rechargeId = rechargeId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = RechargeStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
