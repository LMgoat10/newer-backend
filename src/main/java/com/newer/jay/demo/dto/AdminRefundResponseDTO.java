package com.newer.jay.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AdminRefundResponseDTO {
    private String id;
    private Integer userId;
    private String userName;
    private Long attractionId;
    private String attractionName;
    private LocalDate visitDate;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String contactName;
    private String contactPhone;
    private String contactIdcard;
    private String address;
    private String status;
    private String statusName;
    private String payMethod;
    private String payMethodName;
    private LocalDateTime payTime;
    private BigDecimal payAmount;
    private String refundReason;
    private LocalDateTime refundTime;
    private BigDecimal refundAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
