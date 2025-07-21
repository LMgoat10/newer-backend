package com.newer.jay.demo.dto;

import lombok.Data;

@Data
public class RefundRequestDTO {
    private Integer userId;         // 用户ID
    private String refundReason;    // 退款原因
}
