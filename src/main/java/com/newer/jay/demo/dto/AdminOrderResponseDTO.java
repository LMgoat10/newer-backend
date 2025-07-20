package com.newer.jay.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderResponseDTO {
    private String id;
    private Integer userId;
    private String userName;
    private Long attractionId;
    private String attractionName;
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
    private String status;
    private String statusName;
    
    // 支付信息
    private String payMethod;
    private String payMethodName;
    private LocalDateTime payTime;
    private BigDecimal payAmount;
    
    // 退款信息
    private String refundReason;
    private LocalDateTime refundTime;
    private BigDecimal refundAmount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
