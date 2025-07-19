package com.newer.jay.demo.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private String paymentMethod;
}
