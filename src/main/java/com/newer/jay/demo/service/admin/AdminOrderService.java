package com.newer.jay.demo.service.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.AdminOrderResponseDTO;
import com.newer.jay.demo.entity.Attraction;
import com.newer.jay.demo.entity.TicketOrder;
import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.mapper.AttractionMapper;
import com.newer.jay.demo.mapper.TicketOrderMapper;
import com.newer.jay.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminOrderService {
    
    @Autowired
    private TicketOrderMapper ticketOrderMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AttractionMapper attractionMapper;
    
    /**
     * 分页查询所有订单
     */
    public Page<AdminOrderResponseDTO> getOrders(Integer page, Integer size, String keyword, String status) {
        // 创建分页对象
        Page<TicketOrder> pageObj = new Page<>(page, size);
        
        // 构建查询条件
        LambdaQueryWrapper<TicketOrder> wrapper = new LambdaQueryWrapper<>();
        
        // 根据关键字搜索（用户名、订单ID、联系人姓名）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(TicketOrder::getId, keyword)
                           .or().like(TicketOrder::getContactName, keyword));
        }
        
        // 根据状态筛选
        if (StringUtils.hasText(status) && !"ALL".equals(status)) {
            try {
                TicketOrder.OrderStatus orderStatus = TicketOrder.OrderStatus.valueOf(status);
                wrapper.eq(TicketOrder::getStatus, orderStatus);
            } catch (IllegalArgumentException e) {
                // 忽略无效的状态值
            }
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(TicketOrder::getCreatedAt);
        
        // 执行分页查询
        Page<TicketOrder> orderPage = ticketOrderMapper.selectPage(pageObj, wrapper);
        
        // 转换为DTO并填充关联信息
        List<AdminOrderResponseDTO> dtoList = orderPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 创建返回的分页对象
        Page<AdminOrderResponseDTO> resultPage = new Page<>();
        resultPage.setCurrent(orderPage.getCurrent());
        resultPage.setSize(orderPage.getSize());
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setPages(orderPage.getPages());
        resultPage.setRecords(dtoList);
        
        return resultPage;
    }
    
    /**
     * 根据订单ID获取订单详情
     */
    public AdminOrderResponseDTO getOrderById(String orderId) {
        TicketOrder order = ticketOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return convertToDTO(order);
    }
    
    /**
     * 删除未付款订单
     */
    @Transactional
    public void deleteUnpaidOrder(String orderId) {
        TicketOrder order = ticketOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只能删除未付款的订单（这里我们假设CANCELLED状态表示未付款）
        if (order.getStatus() != TicketOrder.OrderStatus.CANCELLED) {
            throw new RuntimeException("只能删除未付款的订单");
        }
        
        ticketOrderMapper.deleteById(orderId);
    }
    
    /**
     * 放票（发货）- 将已付款订单状态改为已放票
     */
    @Transactional
    public void releaseTicket(String orderId) {
        TicketOrder order = ticketOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只能对已付款的订单进行放票
        if (order.getStatus() != TicketOrder.OrderStatus.PAID) {
            throw new RuntimeException("只能对已付款的订单进行放票操作");
        }
        
        // 更新订单状态为已放票
        order.setStatus(TicketOrder.OrderStatus.TICKETED);
        order.setUpdatedAt(LocalDateTime.now());
        
        ticketOrderMapper.updateById(order);
    }
    
    /**
     * 批量放票
     */
    @Transactional
    public void batchReleaseTickets(List<String> orderIds) {
        for (String orderId : orderIds) {
            try {
                releaseTicket(orderId);
            } catch (Exception e) {
                // 记录错误但继续处理其他订单
                System.err.println("批量放票失败，订单ID: " + orderId + ", 错误: " + e.getMessage());
            }
        }
    }
    
    /**
     * 获取订单统计信息
     */
    public OrderStatisticsDTO getOrderStatistics() {
        // 查询各种状态的订单数量
        long totalOrders = ticketOrderMapper.selectCount(null);
        
        long paidOrders = ticketOrderMapper.selectCount(
            new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getStatus, TicketOrder.OrderStatus.PAID)
        );
        
        long ticketedOrders = ticketOrderMapper.selectCount(
            new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getStatus, TicketOrder.OrderStatus.TICKETED)
        );
        
        long refundRequestOrders = ticketOrderMapper.selectCount(
            new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getStatus, TicketOrder.OrderStatus.REFUND_REQUEST)
        );
        
        long refundedOrders = ticketOrderMapper.selectCount(
            new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getStatus, TicketOrder.OrderStatus.REFUNDED)
        );
        
        long cancelledOrders = ticketOrderMapper.selectCount(
            new LambdaQueryWrapper<TicketOrder>()
                .eq(TicketOrder::getStatus, TicketOrder.OrderStatus.CANCELLED)
        );
        
        return new OrderStatisticsDTO(totalOrders, paidOrders, ticketedOrders, 
                                    refundRequestOrders, refundedOrders, cancelledOrders);
    }
    
    /**
     * 转换实体为DTO
     */
    private AdminOrderResponseDTO convertToDTO(TicketOrder order) {
        AdminOrderResponseDTO dto = new AdminOrderResponseDTO();
        
        // 基本订单信息
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setAttractionId(order.getAttractionId());
        dto.setVisitDate(order.getVisitDate());
        dto.setQuantity(order.getQuantity());
        dto.setUnitPrice(order.getUnitPrice());
        dto.setTotalAmount(order.getTotalAmount());
        
        // 联系人信息
        dto.setContactName(order.getContactName());
        dto.setContactPhone(order.getContactPhone());
        dto.setContactIdcard(order.getContactIdcard());
        dto.setAddress(order.getAddress());
        
        // 订单状态
        dto.setStatus(order.getStatus().name());
        dto.setStatusName(getStatusName(order.getStatus()));
        
        // 支付信息
        if (order.getPayMethod() != null) {
            dto.setPayMethod(order.getPayMethod().name());
            dto.setPayMethodName(getPayMethodName(order.getPayMethod()));
        }
        dto.setPayTime(order.getPayTime());
        dto.setPayAmount(order.getPayAmount());
        
        // 退款信息
        dto.setRefundReason(order.getRefundReason());
        dto.setRefundTime(order.getRefundTime());
        dto.setRefundAmount(order.getRefundAmount());
        
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        // 填充用户信息
        if (order.getUserId() != null) {
            User user = userMapper.selectById(order.getUserId().longValue());
            if (user != null) {
                dto.setUserName(user.getName());
            }
        }
        
        // 填充景点信息
        if (order.getAttractionId() != null) {
            Attraction attraction = attractionMapper.selectById(order.getAttractionId());
            if (attraction != null) {
                dto.setAttractionName(attraction.getName());
            }
        }
        
        return dto;
    }
    
    /**
     * 获取状态中文名称
     */
    private String getStatusName(TicketOrder.OrderStatus status) {
        switch (status) {
            case PAID: return "已支付待放票";
            case TICKETED: return "已放票";
            case REFUND_REQUEST: return "退票申请中";
            case REFUNDED: return "已退款";
            case CANCELLED: return "已取消";
            default: return status.name();
        }
    }
    
    /**
     * 获取支付方式中文名称
     */
    private String getPayMethodName(TicketOrder.PayMethod payMethod) {
        switch (payMethod) {
            case BALANCE: return "余额支付";
            case ALIPAY: return "支付宝";
            case WECHAT: return "微信支付";
            default: return payMethod.name();
        }
    }
    
    /**
     * 订单统计DTO
     */
    public static class OrderStatisticsDTO {
        private long totalOrders;
        private long paidOrders;
        private long ticketedOrders;
        private long refundRequestOrders;
        private long refundedOrders;
        private long cancelledOrders;
        
        public OrderStatisticsDTO(long totalOrders, long paidOrders, long ticketedOrders,
                                 long refundRequestOrders, long refundedOrders, long cancelledOrders) {
            this.totalOrders = totalOrders;
            this.paidOrders = paidOrders;
            this.ticketedOrders = ticketedOrders;
            this.refundRequestOrders = refundRequestOrders;
            this.refundedOrders = refundedOrders;
            this.cancelledOrders = cancelledOrders;
        }
        
        // Getters
        public long getTotalOrders() { return totalOrders; }
        public long getPaidOrders() { return paidOrders; }
        public long getTicketedOrders() { return ticketedOrders; }
        public long getRefundRequestOrders() { return refundRequestOrders; }
        public long getRefundedOrders() { return refundedOrders; }
        public long getCancelledOrders() { return cancelledOrders; }
    }
}
