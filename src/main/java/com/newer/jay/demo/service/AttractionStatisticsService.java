package com.newer.jay.demo.service;

import com.newer.jay.demo.dto.AttractionStatisticsDTO;
import com.newer.jay.demo.mapper.TicketOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 景点统计服务
 */
@Service
public class AttractionStatisticsService {
    
    @Autowired
    private TicketOrderMapper ticketOrderMapper;
    
    /**
     * 获取景点统计排行榜（按票数排序）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制条数
     * @return 景点统计列表
     */
    public List<AttractionStatisticsDTO> getAttractionRankingByTickets(LocalDate startDate, LocalDate endDate, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认显示前10条
        }
        return ticketOrderMapper.getAttractionStatisticsByTickets(startDate, endDate, limit);
    }
    
    /**
     * 获取景点统计排行榜（按收入排序）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制条数
     * @return 景点统计列表
     */
    public List<AttractionStatisticsDTO> getAttractionRankingByRevenue(LocalDate startDate, LocalDate endDate, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认显示前10条
        }
        return ticketOrderMapper.getAttractionStatisticsByRevenue(startDate, endDate, limit);
    }
    
    /**
     * 获取所有景点统计信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 景点统计列表
     */
    public List<AttractionStatisticsDTO> getAllAttractionStatistics(LocalDate startDate, LocalDate endDate) {
        return ticketOrderMapper.getAllAttractionStatistics(startDate, endDate);
    }
    
    /**
     * 获取指定景点的统计信息
     * @param attractionId 景点ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 景点统计信息
     */
    public AttractionStatisticsDTO getAttractionStatisticsById(Integer attractionId, LocalDate startDate, LocalDate endDate) {
        return ticketOrderMapper.getAttractionStatisticsById(attractionId, startDate, endDate);
    }
    
    /**
     * 获取景点统计汇总信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 汇总统计数据
     */
    public Map<String, Object> getAttractionSummary(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> summary = new HashMap<>();
        
        // 总景点数量
        Integer totalAttractions = ticketOrderMapper.getTotalAttractionsWithOrders(startDate, endDate);
        summary.put("totalAttractions", totalAttractions);
        
        // 总订单数量
        Integer totalOrders = ticketOrderMapper.getTotalAttractionOrders(startDate, endDate);
        summary.put("totalOrders", totalOrders);
        
        // 总票数
        Integer totalTickets = ticketOrderMapper.getTotalAttractionTickets(startDate, endDate);
        summary.put("totalTickets", totalTickets);
        
        // 总收入
        Double totalRevenue = ticketOrderMapper.getTotalAttractionRevenue(startDate, endDate);
        summary.put("totalRevenue", totalRevenue);
        
        // 平均每景点订单数
        if (totalAttractions != null && totalAttractions > 0) {
            double avgOrdersPerAttraction = (double) totalOrders / totalAttractions;
            summary.put("averageOrdersPerAttraction", Math.round(avgOrdersPerAttraction * 100.0) / 100.0);
        } else {
            summary.put("averageOrdersPerAttraction", 0.0);
        }
        
        // 平均每景点票数
        if (totalAttractions != null && totalAttractions > 0) {
            double avgTicketsPerAttraction = (double) totalTickets / totalAttractions;
            summary.put("averageTicketsPerAttraction", Math.round(avgTicketsPerAttraction * 100.0) / 100.0);
        } else {
            summary.put("averageTicketsPerAttraction", 0.0);
        }
        
        return summary;
    }
    
    /**
     * 获取最近N天的景点统计排行榜
     * @param days 天数
     * @param limit 限制条数
     * @return 景点统计列表
     */
    public List<AttractionStatisticsDTO> getRecentDaysAttractionRanking(Integer days, Integer limit) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        return getAttractionRankingByTickets(startDate, endDate, limit);
    }
    
    /**
     * 获取今日景点统计排行榜
     * @param limit 限制条数
     * @return 景点统计列表
     */
    public List<AttractionStatisticsDTO> getTodayAttractionRanking(Integer limit) {
        LocalDate today = LocalDate.now();
        return getAttractionRankingByTickets(today, today, limit);
    }
}
