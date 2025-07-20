package com.newer.jay.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CartItemRequestDTO {
    private Long attractionId;
    private String ticketType;      // adult, student, child
    private BigDecimal ticketPrice;
    private Integer quantity;
    private LocalDate visitDate;
}
