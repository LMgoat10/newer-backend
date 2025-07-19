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
@TableName("refund")
public class Refund {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String refundId;
    private String orderId;
    private Long userId;
    private BigDecimal amount;
    private String reason;
    private RefundStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String adminRemark;
    
    public enum RefundStatus {
        PENDING, APPROVED, REJECTED, COMPLETED
    }
    
    public Refund(String refundId, String orderId, Long userId, BigDecimal amount, String reason) {
        this.refundId = refundId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.reason = reason;
        this.status = RefundStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
