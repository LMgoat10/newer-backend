package com.newer.jay.demo.dto;

import lombok.Data;
import com.newer.jay.demo.entity.Attraction;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AttractionResponseDTO {
    private String id;              // 转换为String以匹配前端
    private String name;
    private String address;
    private String areaName;
    private String cityName;
    private String proName;         // 对应provinceName
    private String summary;         // 对应description
    private Location location;
    private List<String> picList;   // 从JSON解析而来
    private List<Ticket> tickets;   // 门票信息
    private BigDecimal rating;
    private Integer reviewCount;
    private String openTime;        // 对应openTimeStr
    private String phone;
    private String website;
    private List<String> tags;      // 从JSON解析而来
    
    @Data
    public static class Location {
        private String lat;
        private String lon;
        
        public Location(String coordinates) {
            // 从coordinates字符串解析经纬度
            // 格式如 "39.916668,116.397026"
            if (coordinates != null && coordinates.contains(",")) {
                String[] parts = coordinates.split(",");
                this.lat = parts.length > 0 ? parts[0].trim() : "";
                this.lon = parts.length > 1 ? parts[1].trim() : "";
            } else {
                this.lat = "";
                this.lon = "";
            }
        }
    }
    
    @Data
    public static class Ticket {
        private String id;
        private String type;
        private String name;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private String description;
        private Integer validDays;
        private Integer stock;
        
        public Ticket(String type, String name, BigDecimal price, Integer stock) {
            this.id = "t" + System.currentTimeMillis() + "_" + type;
            this.type = type;
            this.name = name;
            this.price = price;
            this.description = name + "门票";
            this.validDays = 1;
            this.stock = stock != null ? stock : 100;
        }
    }
    
    // 从Attraction实体转换为DTO
    public static AttractionResponseDTO fromEntity(Attraction attraction) {
        AttractionResponseDTO dto = new AttractionResponseDTO();
        dto.setId(String.valueOf(attraction.getId()));
        dto.setName(attraction.getName());
        dto.setAddress(attraction.getAddress());
        dto.setAreaName(attraction.getAreaName());
        dto.setCityName(attraction.getCityName());
        dto.setProName(attraction.getProvinceName());
        dto.setSummary(attraction.getDescription());
        dto.setLocation(new Location(attraction.getLocation()));
        dto.setRating(attraction.getRating());
        dto.setReviewCount(attraction.getReviewCount());
        dto.setOpenTime(attraction.getOpenTimeStr());
        dto.setPhone(attraction.getPhone());
        dto.setWebsite(attraction.getWebsite());
        
        // 解析JSON字段
        dto.setPicList(parseJsonArray(attraction.getPicList()));
        dto.setTags(parseJsonArray(attraction.getTags()));
        
        // 生成门票信息
        dto.setTickets(generateTickets(attraction));
        
        return dto;
    }
    
    private static List<String> parseJsonArray(String jsonStr) {
        // 简单的JSON数组解析，实际项目中可以使用Jackson或Gson
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return List.of();
        }
        try {
            jsonStr = jsonStr.replace("[", "").replace("]", "").replace("\"", "");
            if (jsonStr.trim().isEmpty()) {
                return List.of();
            }
            return List.of(jsonStr.split(","));
        } catch (Exception e) {
            return List.of();
        }
    }
    
    private static List<Ticket> generateTickets(Attraction attraction) {
        return List.of(
            new Ticket("adult", "成人票", attraction.getPrice(), attraction.getAvailableTickets()),
            new Ticket("student", "学生票", attraction.getPrice().multiply(new BigDecimal("0.5")), 
                      attraction.getAvailableTickets() / 2),
            new Ticket("child", "儿童票", attraction.getPrice().multiply(new BigDecimal("0.3")), 
                      attraction.getAvailableTickets() / 4)
        );
    }
}
