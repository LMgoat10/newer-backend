package com.newer.jay.demo.dto;

import lombok.Data;

@Data
public class RefundRequest {
    private String orderId;
    private String reason;
}
