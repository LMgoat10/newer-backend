package com.newer.jay.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
}
