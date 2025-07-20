package com.newer.jay.demo.dto;

import lombok.Data;
import com.newer.jay.demo.entity.CartItem;
import com.newer.jay.demo.entity.Attraction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItemResponseDTO {
    private String id;           // 对应前端的字符串ID
    private String spotId;       // 景点ID（对应attractionId）
    private String spotName;     // 景点名称
    private String spotImage;    // 景点图片
    private String ticketId;     // 门票ID
    private String ticketType;   // 门票类型
    private String ticketName;   // 门票名称
    private BigDecimal price;    // 价格
    private BigDecimal originalPrice; // 原价
    private Integer quantity;    // 数量
    private Integer validDays;   // 有效天数
    private LocalDateTime addedAt; // 添加时间
    
    // 从CartItem实体和Attraction实体组合转换
    public static CartItemResponseDTO fromEntities(CartItem cartItem, Attraction attraction) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(String.valueOf(cartItem.getId()));
        dto.setSpotId(String.valueOf(cartItem.getAttractionId()));
        dto.setSpotName(attraction.getName());
        
        // 从attraction的picList JSON中获取第一张图片
        dto.setSpotImage(extractFirstImage(attraction.getPicList()));
        
        // 根据门票类型生成对应信息
        dto.setTicketId(generateTicketId(cartItem));
        dto.setTicketType(cartItem.getTicketType());
        dto.setTicketName(generateTicketName(cartItem.getTicketType()));
        dto.setPrice(cartItem.getTicketPrice());
        dto.setOriginalPrice(calculateOriginalPrice(cartItem.getTicketPrice(), cartItem.getTicketType()));
        dto.setQuantity(cartItem.getQuantity());
        dto.setValidDays(1); // 默认1天有效
        dto.setAddedAt(cartItem.getCreatedAt());
        
        return dto;
    }
    
    private static String extractFirstImage(String picListJson) {
        if (picListJson == null || picListJson.trim().isEmpty()) {
            return "https://via.placeholder.com/400x200?text=No+Image";
        }
        try {
            // 简单解析JSON数组，获取第一张图片
            String cleaned = picListJson.replace("[", "").replace("]", "").replace("\"", "");
            String[] images = cleaned.split(",");
            return images.length > 0 ? images[0].trim() : "https://via.placeholder.com/400x200?text=No+Image";
        } catch (Exception e) {
            return "https://via.placeholder.com/400x200?text=No+Image";
        }
    }
    
    private static String generateTicketId(CartItem cartItem) {
        return "cart_" + cartItem.getId() + "_" + cartItem.getTicketType();
    }
    
    private static String generateTicketName(String ticketType) {
        return switch (ticketType.toLowerCase()) {
            case "adult" -> "成人票";
            case "student" -> "学生票";
            case "child" -> "儿童票";
            default -> ticketType + "票";
        };
    }
    
    private static BigDecimal calculateOriginalPrice(BigDecimal currentPrice, String ticketType) {
        // 如果是折扣票，计算原价
        return switch (ticketType.toLowerCase()) {
            case "student" -> currentPrice.multiply(new BigDecimal("2")); // 学生票是5折，所以原价是2倍
            case "child" -> currentPrice.multiply(new BigDecimal("3.33")); // 儿童票是3折，所以原价约是3.33倍
            default -> null; // 成人票没有原价概念
        };
    }
}

@Data
class CartSummaryResponseDTO {
    private Integer totalItems;
    private BigDecimal totalPrice;
    private BigDecimal totalSavings;
    
    public CartSummaryResponseDTO(Integer totalItems, BigDecimal totalPrice, BigDecimal totalSavings) {
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
        this.totalSavings = totalSavings != null ? totalSavings : BigDecimal.ZERO;
    }
}
