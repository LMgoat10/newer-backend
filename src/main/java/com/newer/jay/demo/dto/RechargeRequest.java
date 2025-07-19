package com.newer.jay.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeRequest {
    private BigDecimal amount;
    private String paymentMethod;
}
