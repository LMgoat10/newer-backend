package com.newer.jay.demo.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 景点统计数据传输对象
 */
public class AttractionStatisticsDTO {
    
    private Integer attractionId;
    private String attractionName;
    private Integer orderCount;        // 订单数量
    private Integer totalTickets;      // 总票数
    private BigDecimal totalRevenue;   // 总收入
    private BigDecimal averageOrderValue; // 平均订单价值
    
    public AttractionStatisticsDTO() {}
    
    public AttractionStatisticsDTO(Integer attractionId, String attractionName, 
                                 Integer orderCount, Integer totalTickets, BigDecimal totalRevenue) {
        this.attractionId = attractionId;
        this.attractionName = attractionName;
        this.orderCount = orderCount;
        this.totalTickets = totalTickets;
        this.totalRevenue = totalRevenue;
        
        // 计算平均订单价值
        if (orderCount != null && orderCount > 0 && totalRevenue != null) {
            this.averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);
        } else {
            this.averageOrderValue = BigDecimal.ZERO;
        }
    }
    
    // Getters and Setters
    public Integer getAttractionId() {
        return attractionId;
    }
    
    public void setAttractionId(Integer attractionId) {
        this.attractionId = attractionId;
    }
    
    public String getAttractionName() {
        return attractionName;
    }
    
    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }
    
    public Integer getOrderCount() {
        return orderCount;
    }
    
    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }
    
    public Integer getTotalTickets() {
        return totalTickets;
    }
    
    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }
    
    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
    
    @Override
    public String toString() {
        return "AttractionStatisticsDTO{" +
                "attractionId=" + attractionId +
                ", attractionName='" + attractionName + '\'' +
                ", orderCount=" + orderCount +
                ", totalTickets=" + totalTickets +
                ", totalRevenue=" + totalRevenue +
                ", averageOrderValue=" + averageOrderValue +
                '}';
    }
}
