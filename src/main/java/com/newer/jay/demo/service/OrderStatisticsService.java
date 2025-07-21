package com.newer.jay.demo.service;

import com.newer.jay.demo.dto.OrderStatisticsDTO;
import com.newer.jay.demo.mapper.TicketOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class OrderStatisticsService {
    
    @Autowired
    private TicketOrderMapper ticketOrderMapper;
    
    /**
     * 获取指定日期范围内每天的订单统计
     */
    public List<OrderStatisticsDTO> getDailyOrderStatistics(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30); // 默认最近30天
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // 确保开始日期不晚于结束日期
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }
        
        return ticketOrderMapper.getDailyOrderStatistics(startDate, endDate);
    }
    
    /**
     * 获取指定用户在指定日期范围内每天的订单统计
     */
    public List<OrderStatisticsDTO> getDailyOrderStatisticsByUser(Integer userId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30); // 默认最近30天
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // 确保开始日期不晚于结束日期
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }
        
        return ticketOrderMapper.getDailyOrderStatisticsByUser(userId, startDate, endDate);
    }
    
    /**
     * 获取订单流水总览
     */
    public Map<String, Object> getOrderSummary(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30); // 默认最近30天
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // 确保开始日期不晚于结束日期
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }
        
        return ticketOrderMapper.getOrderSummary(startDate, endDate);
    }
    
    /**
     * 获取最近N天的订单统计
     */
    public List<OrderStatisticsDTO> getRecentDaysStatistics(Integer days) {
        if (days == null || days <= 0) {
            days = 7; // 默认最近7天
        }
        if (days > 365) {
            days = 365; // 最多查询一年
        }
        
        return ticketOrderMapper.getRecentDaysStatistics(days);
    }
    
    /**
     * 获取今日订单统计
     */
    public OrderStatisticsDTO getTodayStatistics() {
        LocalDate today = LocalDate.now();
        List<OrderStatisticsDTO> todayStats = ticketOrderMapper.getDailyOrderStatistics(today, today);
        
        if (todayStats.isEmpty()) {
            return new OrderStatisticsDTO(today, 0L, null);
        }
        
        return todayStats.get(0);
    }
    
    /**
     * 获取本月订单统计
     */
    public List<OrderStatisticsDTO> getThisMonthStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        
        return ticketOrderMapper.getDailyOrderStatistics(firstDayOfMonth, today);
    }
}
