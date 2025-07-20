package com.newer.jay.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AttractionRequestDTO {
    private String name;
    private String description;
    private String location;
    private String address;           // 详细地址
    private String cityName;          // 城市名称
    private String provinceName;      // 省份名称
    private String areaName;          // 区域名称
    
    // 基础价格（成人票价格）
    private BigDecimal price;
    private Integer totalTickets;
    private Integer availableTickets;
    
    // 营业时间
    private String openTime;          // 格式: "08:30"
    private String closeTime;         // 格式: "17:00"
    private String openTimeStr;       // 开放时间文本描述
    
    // 评分和评价
    private BigDecimal rating;        // 评分（如4.5）
    private Integer reviewCount;      // 评价数量
    
    // 联系方式
    private String phone;
    private String website;
    
    // 图片列表
    private List<String> picList;     // 图片URL列表
    
    // 标签列表
    private List<String> tags;        // 标签列表
    
    private String status;            // ACTIVE 或 INACTIVE
}
