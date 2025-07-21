package com.newer.jay.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatisticsDTO {
    
    /**
     * 统计日期
     */
    private LocalDate date;
    
    /**
     * 当日订单数量
     */
    private Long orderCount;
    
    /**
     * 当日流水金额（已支付订单的总金额）
     */
    private BigDecimal totalAmount;
    
    /**
     * 当日平均订单金额
     */
    private BigDecimal averageAmount;
    
    public OrderStatisticsDTO(LocalDate date, Long orderCount, BigDecimal totalAmount) {
        this.date = date;
        this.orderCount = orderCount;
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        
        // 计算平均订单金额
        if (orderCount != null && orderCount > 0 && this.totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.averageAmount = this.totalAmount.divide(new BigDecimal(orderCount), 2, RoundingMode.HALF_UP);
        } else {
            this.averageAmount = BigDecimal.ZERO;
        }
    }
}
