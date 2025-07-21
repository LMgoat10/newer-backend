package com.newer.jay.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.AttractionStatisticsDTO;
import com.newer.jay.demo.dto.OrderStatisticsDTO;
import com.newer.jay.demo.entity.TicketOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface TicketOrderMapper extends BaseMapper<TicketOrder> {
    
    /**
     * 分页查询用户订单 - 简化版本
     */
    @Select("SELECT * FROM ticket_order WHERE user_id = #{userId} ORDER BY created_at DESC")
    IPage<TicketOrder> selectUserOrdersPage(Page<TicketOrder> page, 
                                           @Param("userId") Integer userId,
                                           @Param("status") String status,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    /**
     * 统计用户订单数量
     */
    @Select("SELECT COUNT(*) FROM ticket_order WHERE user_id = #{userId} AND status = #{status}")
    Integer countUserOrdersByStatus(@Param("userId") Integer userId, @Param("status") String status);
    
    /**
     * 获取用户订单统计
     */
    @Select("SELECT status, COUNT(*) as count FROM ticket_order WHERE user_id = #{userId} GROUP BY status")
    List<Map<String, Object>> getUserOrderStats(@Param("userId") Integer userId);
    
    /**
     * 按日期统计订单流水 - 所有订单
     * 统计指定日期范围内每天的订单数量和流水金额
     */
    @Select("SELECT " +
            "DATE(created_at) as date, " +
            "COUNT(*) as orderCount, " +
            "COALESCE(SUM(CASE WHEN status IN ('PAID', 'TICKETED') THEN total_amount ELSE 0 END), 0) as totalAmount " +
            "FROM ticket_order " +
            "WHERE DATE(created_at) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date DESC")
    List<OrderStatisticsDTO> getDailyOrderStatistics(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);
    
    /**
     * 按日期统计订单流水 - 指定用户
     * 统计指定用户在指定日期范围内每天的订单数量和流水金额
     */
    @Select("SELECT " +
            "DATE(created_at) as date, " +
            "COUNT(*) as orderCount, " +
            "COALESCE(SUM(CASE WHEN status IN ('PAID', 'TICKETED') THEN total_amount ELSE 0 END), 0) as totalAmount " +
            "FROM ticket_order " +
            "WHERE user_id = #{userId} AND DATE(created_at) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date DESC")
    List<OrderStatisticsDTO> getDailyOrderStatisticsByUser(@Param("userId") Integer userId,
                                                           @Param("startDate") LocalDate startDate, 
                                                           @Param("endDate") LocalDate endDate);
    
    /**
     * 获取指定日期范围内的订单流水总览
     */
    @Select("SELECT " +
            "COUNT(*) as totalOrders, " +
            "COUNT(CASE WHEN status IN ('PAID', 'TICKETED') THEN 1 END) as paidOrders, " +
            "COALESCE(SUM(CASE WHEN status IN ('PAID', 'TICKETED') THEN total_amount ELSE 0 END), 0) as totalRevenue, " +
            "COALESCE(AVG(CASE WHEN status IN ('PAID', 'TICKETED') THEN total_amount END), 0) as averageOrderValue " +
            "FROM ticket_order " +
            "WHERE DATE(created_at) BETWEEN #{startDate} AND #{endDate}")
    Map<String, Object> getOrderSummary(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    
    /**
     * 获取最近N天的订单流水统计
     */
    @Select("SELECT " +
            "DATE(created_at) as date, " +
            "COUNT(*) as orderCount, " +
            "COALESCE(SUM(CASE WHEN status IN ('PAID', 'TICKETED') THEN total_amount ELSE 0 END), 0) as totalAmount " +
            "FROM ticket_order " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date DESC")
    List<OrderStatisticsDTO> getRecentDaysStatistics(@Param("days") Integer days);
    
    // ====================== 景点统计相关方法 ======================
    
    /**
     * 获取景点统计排行榜（按票数排序）
     */
    @Select("SELECT " +
            "t.attraction_id as attractionId, " +
            "a.name as attractionName, " +
            "COUNT(t.id) as orderCount, " +
            "COALESCE(SUM(t.quantity), 0) as totalTickets, " +
            "COALESCE(SUM(CASE WHEN t.status IN ('PAID', 'TICKETED') THEN t.total_amount ELSE 0 END), 0) as totalRevenue " +
            "FROM ticket_order t " +
            "LEFT JOIN attraction a ON t.attraction_id = a.id " +
            "WHERE (#{startDate} IS NULL OR DATE(t.created_at) >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR DATE(t.created_at) <= #{endDate}) " +
            "GROUP BY t.attraction_id, a.name " +
            "ORDER BY totalTickets DESC " +
            "LIMIT #{limit}")
    List<AttractionStatisticsDTO> getAttractionStatisticsByTickets(@Param("startDate") LocalDate startDate, 
                                                                  @Param("endDate") LocalDate endDate, 
                                                                  @Param("limit") Integer limit);
    
    /**
     * 获取景点统计排行榜（按收入排序）
     */
    @Select("SELECT " +
            "t.attraction_id as attractionId, " +
            "a.name as attractionName, " +
            "COUNT(t.id) as orderCount, " +
            "COALESCE(SUM(t.quantity), 0) as totalTickets, " +
            "COALESCE(SUM(CASE WHEN t.status IN ('PAID', 'TICKETED') THEN t.total_amount ELSE 0 END), 0) as totalRevenue " +
            "FROM ticket_order t " +
            "LEFT JOIN attraction a ON t.attraction_id = a.id " +
            "WHERE (#{startDate} IS NULL OR DATE(t.created_at) >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR DATE(t.created_at) <= #{endDate}) " +
            "GROUP BY t.attraction_id, a.name " +
            "ORDER BY totalRevenue DESC " +
            "LIMIT #{limit}")
    List<AttractionStatisticsDTO> getAttractionStatisticsByRevenue(@Param("startDate") LocalDate startDate, 
                                                                  @Param("endDate") LocalDate endDate, 
                                                                  @Param("limit") Integer limit);
    
    /**
     * 获取所有景点统计信息
     */
    @Select("SELECT " +
            "t.attraction_id as attractionId, " +
            "a.name as attractionName, " +
            "COUNT(t.id) as orderCount, " +
            "COALESCE(SUM(t.quantity), 0) as totalTickets, " +
            "COALESCE(SUM(CASE WHEN t.status IN ('PAID', 'TICKETED') THEN t.total_amount ELSE 0 END), 0) as totalRevenue " +
            "FROM ticket_order t " +
            "LEFT JOIN attraction a ON t.attraction_id = a.id " +
            "WHERE (#{startDate} IS NULL OR DATE(t.created_at) >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR DATE(t.created_at) <= #{endDate}) " +
            "GROUP BY t.attraction_id, a.name " +
            "ORDER BY totalTickets DESC")
    List<AttractionStatisticsDTO> getAllAttractionStatistics(@Param("startDate") LocalDate startDate, 
                                                             @Param("endDate") LocalDate endDate);
    
    /**
     * 获取指定景点的统计信息
     */
    @Select("SELECT " +
            "t.attraction_id as attractionId, " +
            "a.name as attractionName, " +
            "COUNT(t.id) as orderCount, " +
            "COALESCE(SUM(t.quantity), 0) as totalTickets, " +
            "COALESCE(SUM(CASE WHEN t.status IN ('PAID', 'TICKETED') THEN t.total_amount ELSE 0 END), 0) as totalRevenue " +
            "FROM ticket_order t " +
            "LEFT JOIN attraction a ON t.attraction_id = a.id " +
            "WHERE t.attraction_id = #{attractionId} " +
            "AND (#{startDate} IS NULL OR DATE(t.created_at) >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR DATE(t.created_at) <= #{endDate}) " +
            "GROUP BY t.attraction_id, a.name")
    AttractionStatisticsDTO getAttractionStatisticsById(@Param("attractionId") Integer attractionId,
                                                        @Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate);
    
    /**
     * 获取有订单的景点总数
     */
    @Select("SELECT COUNT(DISTINCT t.attraction_id) " +
            "FROM ticket_order t " +
            "WHERE (#{startDate} IS NULL OR DATE(t.created_at) >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR DATE(t.created_at) <= #{endDate})")
    Integer getTotalAttractionsWithOrders(@Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
    
    /**
     * 获取景点订单总数
     */
    @Select("SELECT COUNT(*) " +
            "FROM ticket_order t " +
            "WHERE (#{startDate} IS NULL OR DATE(t.created_at) >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR DATE(t.created_at) <= #{endDate})")
    Integer getTotalAttractionOrders(@Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);
    
    /**
     * 获取景点总票数
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) " +
            "FROM ticket_order t " +
            "WHERE (#{startDate} IS NULL OR DATE(t.created_at) >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR DATE(t.created_at) <= #{endDate})")
    Integer getTotalAttractionTickets(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);
    
    /**
     * 获取景点总收入
     */
    @Select("SELECT COALESCE(SUM(CASE WHEN status IN ('PAID', 'TICKETED') THEN total_amount ELSE 0 END), 0) " +
            "FROM ticket_order t " +
            "WHERE (#{startDate} IS NULL OR DATE(t.created_at) >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR DATE(t.created_at) <= #{endDate})")
    Double getTotalAttractionRevenue(@Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);
}
