package com.newer.jay.demo.controller;

import com.newer.jay.demo.dto.ApiResponse;
import com.newer.jay.demo.dto.AttractionStatisticsDTO;
import com.newer.jay.demo.dto.OrderStatisticsDTO;
import com.newer.jay.demo.service.AttractionStatisticsService;
import com.newer.jay.demo.service.OrderStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderStatisticsController {
    
    @Autowired
    private OrderStatisticsService orderStatisticsService;
    
    @Autowired
    private AttractionStatisticsService attractionStatisticsService;
    
    /**
     * 获取指定日期范围内每天的订单统计
     */
    @GetMapping("/admin/orders/statistics/daily")
    public ApiResponse<List<OrderStatisticsDTO>> getDailyOrderStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            List<OrderStatisticsDTO> statistics = orderStatisticsService.getDailyOrderStatistics(startDate, endDate);
            return ApiResponse.success("查询成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定用户在指定日期范围内每天的订单统计
     */
    @GetMapping("/user/orders/statistics/daily")
    public ApiResponse<List<OrderStatisticsDTO>> getDailyOrderStatisticsByUser(
            @RequestParam Integer userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            List<OrderStatisticsDTO> statistics = orderStatisticsService.getDailyOrderStatisticsByUser(userId, startDate, endDate);
            return ApiResponse.success("查询成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单流水总览
     */
    @GetMapping("/admin/orders/statistics/summary")
    public ApiResponse<Map<String, Object>> getOrderSummary(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            Map<String, Object> summary = orderStatisticsService.getOrderSummary(startDate, endDate);
            return ApiResponse.success("查询成功", summary);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近N天的订单统计
     */
    @GetMapping("/admin/orders/statistics/recent")
    public ApiResponse<List<OrderStatisticsDTO>> getRecentDaysStatistics(
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        
        try {
            List<OrderStatisticsDTO> statistics = orderStatisticsService.getRecentDaysStatistics(days);
            return ApiResponse.success("查询成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取今日订单统计
     */
    @GetMapping("/admin/orders/statistics/today")
    public ApiResponse<OrderStatisticsDTO> getTodayStatistics() {
        try {
            OrderStatisticsDTO todayStats = orderStatisticsService.getTodayStatistics();
            return ApiResponse.success("查询成功", todayStats);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取本月订单统计
     */
    @GetMapping("/admin/orders/statistics/this-month")
    public ApiResponse<List<OrderStatisticsDTO>> getThisMonthStatistics() {
        try {
            List<OrderStatisticsDTO> monthStats = orderStatisticsService.getThisMonthStatistics();
            return ApiResponse.success("查询成功", monthStats);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单统计仪表板数据
     */
    @GetMapping("/admin/orders/statistics/dashboard")
    public ApiResponse<Map<String, Object>> getDashboardStatistics() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // 今日统计
            OrderStatisticsDTO todayStats = orderStatisticsService.getTodayStatistics();
            dashboard.put("todayStatistics", todayStats);
            
            // 最近7天统计
            List<OrderStatisticsDTO> recentStats = orderStatisticsService.getRecentDaysStatistics(7);
            dashboard.put("recentStatistics", recentStats);
            
            // 本月总览
            Map<String, Object> monthSummary = orderStatisticsService.getOrderSummary(
                LocalDate.now().withDayOfMonth(1), LocalDate.now());
            dashboard.put("monthSummary", monthSummary);
            
            return ApiResponse.success("查询成功", dashboard);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    // ====================== 景点统计相关接口 ======================
    
    /**
     * 获取景点统计排行榜（按票数排序）
     */
    @GetMapping("/admin/attractions/statistics/ranking-by-tickets")
    public ApiResponse<List<AttractionStatisticsDTO>> getAttractionRankingByTickets(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        try {
            List<AttractionStatisticsDTO> statistics = attractionStatisticsService.getAttractionRankingByTickets(startDate, endDate, limit);
            return ApiResponse.success("查询成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取景点统计排行榜（按收入排序）
     */
    @GetMapping("/admin/attractions/statistics/ranking-by-revenue")
    public ApiResponse<List<AttractionStatisticsDTO>> getAttractionRankingByRevenue(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        try {
            List<AttractionStatisticsDTO> statistics = attractionStatisticsService.getAttractionRankingByRevenue(startDate, endDate, limit);
            return ApiResponse.success("查询成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有景点统计信息
     */
    @GetMapping("/admin/attractions/statistics/all")
    public ApiResponse<List<AttractionStatisticsDTO>> getAllAttractionStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            List<AttractionStatisticsDTO> statistics = attractionStatisticsService.getAllAttractionStatistics(startDate, endDate);
            return ApiResponse.success("查询成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定景点的统计信息
     */
    @GetMapping("/admin/attractions/statistics/{attractionId}")
    public ApiResponse<AttractionStatisticsDTO> getAttractionStatisticsById(
            @PathVariable Integer attractionId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            AttractionStatisticsDTO statistics = attractionStatisticsService.getAttractionStatisticsById(attractionId, startDate, endDate);
            return ApiResponse.success("查询成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取景点统计汇总信息
     */
    @GetMapping("/admin/attractions/statistics/summary")
    public ApiResponse<Map<String, Object>> getAttractionSummary(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            Map<String, Object> summary = attractionStatisticsService.getAttractionSummary(startDate, endDate);
            return ApiResponse.success("查询成功", summary);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近N天的景点统计排行榜
     */
    @GetMapping("/admin/attractions/statistics/recent")
    public ApiResponse<List<AttractionStatisticsDTO>> getRecentDaysAttractionRanking(
            @RequestParam(required = false, defaultValue = "7") Integer days,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        try {
            List<AttractionStatisticsDTO> statistics = attractionStatisticsService.getRecentDaysAttractionRanking(days, limit);
            return ApiResponse.success("查询成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取今日景点统计排行榜
     */
    @GetMapping("/admin/attractions/statistics/today")
    public ApiResponse<List<AttractionStatisticsDTO>> getTodayAttractionRanking(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        
        try {
            List<AttractionStatisticsDTO> statistics = attractionStatisticsService.getTodayAttractionRanking(limit);
            return ApiResponse.success("查询成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取景点统计仪表板数据
     */
    @GetMapping("/admin/attractions/statistics/dashboard")
    public ApiResponse<Map<String, Object>> getAttractionDashboardStatistics() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // 今日景点排行榜
            List<AttractionStatisticsDTO> todayRanking = attractionStatisticsService.getTodayAttractionRanking(5);
            dashboard.put("todayRanking", todayRanking);
            
            // 最近7天景点排行榜
            List<AttractionStatisticsDTO> recentRanking = attractionStatisticsService.getRecentDaysAttractionRanking(7, 10);
            dashboard.put("recentRanking", recentRanking);
            
            // 本月景点汇总
            Map<String, Object> monthSummary = attractionStatisticsService.getAttractionSummary(
                LocalDate.now().withDayOfMonth(1), LocalDate.now());
            dashboard.put("monthSummary", monthSummary);
            
            return ApiResponse.success("查询成功", dashboard);
        } catch (Exception e) {
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }
    
}
