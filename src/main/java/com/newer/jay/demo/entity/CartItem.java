package com.newer.jay.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("cart_item")
public class CartItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Integer userId;        // 改为Integer以匹配user表的int类型
    private Long attractionId;
    private String ticketType;        // 门票类型（如adult, student, child）
    private BigDecimal ticketPrice;   // 门票价格
    private Integer quantity;
    private LocalDate visitDate;      // 游玩日期
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数用于创建新购物车项
    public CartItem(Integer userId, Long attractionId, String ticketType, 
                   BigDecimal ticketPrice, Integer quantity, LocalDate visitDate) {
        this.userId = userId;
        this.attractionId = attractionId;
        this.ticketType = ticketType;
        this.ticketPrice = ticketPrice;
        this.quantity = quantity;
        this.visitDate = visitDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
