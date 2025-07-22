package com.newer.jay.demo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Attraction实体类测试 - 基于JUnit5
 */
@DisplayName("Attraction实体类测试")
public class AttractionTest {

    private Attraction attraction;

    @BeforeEach
    void setUp() {
        attraction = new Attraction();
    }

    @Test
    @DisplayName("测试Attraction构造函数")
    void testAttractionConstructors() {
        // 测试无参构造函数
        Attraction attraction1 = new Attraction();
        assertNotNull(attraction1);

        // 测试有参构造函数
        LocalTime openTime = LocalTime.of(9, 0);
        LocalTime closeTime = LocalTime.of(18, 0);
        
        Attraction attraction2 = new Attraction("测试景点", "景点描述", "北京市", "详细地址",
                "北京", "北京市", "朝阳区", new BigDecimal("100.00"), 1000,
                openTime, closeTime, "09:00-18:00", "010-12345678", 
                "http://test.com", "[]", "[]");
        
        assertEquals("测试景点", attraction2.getName());
        assertEquals("景点描述", attraction2.getDescription());
        assertEquals(new BigDecimal("100.00"), attraction2.getPrice());
        assertEquals(Attraction.AttractionStatus.ACTIVE, attraction2.getStatus());
        assertEquals(Integer.valueOf(1000), attraction2.getTotalTickets());
        assertEquals(Integer.valueOf(1000), attraction2.getAvailableTickets()); // 默认等于totalTickets
    }

    @Test
    @DisplayName("测试Attraction的基本字段设置")
    void testAttractionBasicFields() {
        attraction.setId(1L);
        attraction.setName("天安门广场");
        attraction.setDescription("中国北京市中心的城市广场");
        attraction.setLocation("北京市东城区");
        attraction.setAddress("北京市东城区东长安街");
        attraction.setCityName("北京");
        attraction.setProvinceName("北京市");
        attraction.setAreaName("东城区");

        assertEquals(Long.valueOf(1L), attraction.getId());
        assertEquals("天安门广场", attraction.getName());
        assertEquals("中国北京市中心的城市广场", attraction.getDescription());
        assertEquals("北京市东城区", attraction.getLocation());
        assertEquals("北京市东城区东长安街", attraction.getAddress());
        assertEquals("北京", attraction.getCityName());
        assertEquals("北京市", attraction.getProvinceName());
        assertEquals("东城区", attraction.getAreaName());
    }

    @Test
    @DisplayName("测试Attraction的价格和门票信息")
    void testAttractionPriceAndTickets() {
        BigDecimal price = new BigDecimal("88.00");
        attraction.setPrice(price);
        attraction.setTotalTickets(1000);
        attraction.setAvailableTickets(800);

        assertEquals(price, attraction.getPrice());
        assertEquals(Integer.valueOf(1000), attraction.getTotalTickets());
        assertEquals(Integer.valueOf(800), attraction.getAvailableTickets());

        // 测试边界值
        attraction.setAvailableTickets(0);
        assertEquals(Integer.valueOf(0), attraction.getAvailableTickets());
    }

    @Test
    @DisplayName("测试Attraction的营业时间")
    void testAttractionBusinessHours() {
        LocalTime openTime = LocalTime.of(8, 30);
        LocalTime closeTime = LocalTime.of(17, 30);
        
        attraction.setOpenTime(openTime);
        attraction.setCloseTime(closeTime);
        attraction.setOpenTimeStr("08:30-17:30");

        assertEquals(openTime, attraction.getOpenTime());
        assertEquals(closeTime, attraction.getCloseTime());
        assertEquals("08:30-17:30", attraction.getOpenTimeStr());
    }

    @Test
    @DisplayName("测试Attraction的评分和评价")
    void testAttractionRatingAndReviews() {
        BigDecimal rating = new BigDecimal("4.8");
        attraction.setRating(rating);
        attraction.setReviewCount(256);

        assertEquals(rating, attraction.getRating());
        assertEquals(Integer.valueOf(256), attraction.getReviewCount());

        // 测试边界值
        attraction.setRating(new BigDecimal("5.0"));
        attraction.setReviewCount(0);
        assertEquals(new BigDecimal("5.0"), attraction.getRating());
        assertEquals(Integer.valueOf(0), attraction.getReviewCount());
    }

    @Test
    @DisplayName("测试Attraction的联系方式")
    void testAttractionContactInfo() {
        attraction.setPhone("010-12345678");
        attraction.setWebsite("http://example.com");

        assertEquals("010-12345678", attraction.getPhone());
        assertEquals("http://example.com", attraction.getWebsite());
    }

    @Test
    @DisplayName("测试Attraction的图片和标签")
    void testAttractionPicsAndTags() {
        String picList = "[\"pic1.jpg\", \"pic2.jpg\"]";
        String tags = "[\"历史\", \"文化\", \"必游\"]";
        
        attraction.setPicList(picList);
        attraction.setTags(tags);

        assertEquals(picList, attraction.getPicList());
        assertEquals(tags, attraction.getTags());
    }

    @Test
    @DisplayName("测试Attraction的时间戳")
    void testAttractionTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        attraction.setCreatedAt(now);
        attraction.setUpdatedAt(now);

        assertEquals(now, attraction.getCreatedAt());
        assertEquals(now, attraction.getUpdatedAt());
    }

    @Test
    @DisplayName("测试Attraction的状态枚举")
    void testAttractionStatus() {
        // 测试ACTIVE状态
        attraction.setStatus(Attraction.AttractionStatus.ACTIVE);
        assertEquals(Attraction.AttractionStatus.ACTIVE, attraction.getStatus());

        // 测试INACTIVE状态
        attraction.setStatus(Attraction.AttractionStatus.INACTIVE);
        assertEquals(Attraction.AttractionStatus.INACTIVE, attraction.getStatus());
    }

    @Test
    @DisplayName("测试Attraction的空值处理")
    void testAttractionNullValues() {
        attraction.setName(null);
        attraction.setDescription(null);
        attraction.setPicList(null);
        attraction.setTags(null);

        assertNull(attraction.getName());
        assertNull(attraction.getDescription());
        assertNull(attraction.getPicList());
        assertNull(attraction.getTags());
    }

    @Test
    @DisplayName("测试Attraction的toString方法")
    void testAttractionToString() {
        attraction.setId(1L);
        attraction.setName("测试景点");
        attraction.setPrice(new BigDecimal("100.00"));

        String attractionString = attraction.toString();
        assertNotNull(attractionString);
        assertTrue(attractionString.contains("测试景点"));
    }
}
