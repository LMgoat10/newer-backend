package com.newer.jay.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeductBalanceRequest {
    private BigDecimal amount;      // 扣除金额
    private String orderId;         // 订单ID
    private String description;     // 扣款描述
    
    // 构造函数
    public DeductBalanceRequest() {}
    
    public DeductBalanceRequest(BigDecimal amount, String orderId, String description) {
        this.amount = amount;
        this.orderId = orderId;
        this.description = description;
    }
}
