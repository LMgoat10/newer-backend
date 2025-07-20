package com.newer.jay.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("attraction")
public class Attraction {
    @TableId(type = IdType.AUTO)
    private Long id;
    
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
    private LocalTime openTime;
    private LocalTime closeTime;
    private String openTimeStr;       // 开放时间文本描述
    
    // 评分和评价
    private BigDecimal rating;        // 评分（如4.5）
    private Integer reviewCount;      // 评价数量
    
    // 联系方式
    private String phone;
    private String website;
    
    // 图片（JSON数组字符串）
    private String picList;           // 存储图片URL的JSON数组
    
    // 标签（JSON数组字符串）
    private String tags;              // 存储标签的JSON数组
    
    private AttractionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum AttractionStatus {
        ACTIVE, INACTIVE
    }
    
    // 构造函数用于创建新景点
    public Attraction(String name, String description, String location, String address,
                     String cityName, String provinceName, String areaName,
                     BigDecimal price, Integer totalTickets, 
                     LocalTime openTime, LocalTime closeTime, String openTimeStr,
                     String phone, String website, String picList, String tags) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.address = address;
        this.cityName = cityName;
        this.provinceName = provinceName;
        this.areaName = areaName;
        this.price = price;
        this.totalTickets = totalTickets;
        this.availableTickets = totalTickets;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.openTimeStr = openTimeStr;
        this.phone = phone;
        this.website = website;
        this.picList = picList;
        this.tags = tags;
        this.rating = BigDecimal.valueOf(4.5); // 默认评分
        this.reviewCount = 0;
        this.status = AttractionStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
