package com.newer.jay.demo.service.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.AdminRefundResponseDTO;
import com.newer.jay.demo.entity.TicketOrder;
import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.entity.Attraction;
import com.newer.jay.demo.mapper.TicketOrderMapper;
import com.newer.jay.demo.mapper.UserMapper;
import com.newer.jay.demo.mapper.AttractionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRefundService {

    private final TicketOrderMapper ticketOrderMapper;
    private final UserMapper userMapper;
    private final AttractionMapper attractionMapper;

    /**
     * 分页查询退票申请列表
     */
    public Page<AdminRefundResponseDTO> getRefundRequests(int page, int size, String keyword, String status) {
        System.out.println("AdminRefundService.getRefundRequests called with page=" + page + ", size=" + size + ", keyword=" + keyword + ", status=" + status);
        
        QueryWrapper<TicketOrder> wrapper = new QueryWrapper<>();
        
        // 只查询退票申请状态的订单
        if ("ALL".equals(status) || status == null || status.isEmpty()) {
            wrapper.eq("status", TicketOrder.OrderStatus.REFUND_REQUEST);
        } else {
            wrapper.eq("status", TicketOrder.OrderStatus.valueOf(status));
        }
        
        // 关键词搜索：订单ID、联系人姓名、联系人电话
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                .like("id", keyword.trim())
                .or().like("contact_name", keyword.trim()) 
                .or().like("contact_phone", keyword.trim())
            );
        }
        
        wrapper.orderByDesc("created_at");
        
        System.out.println("Executing query...");
        Page<TicketOrder> orderPage = ticketOrderMapper.selectPage(new Page<>(page, size), wrapper);
        System.out.println("Query result: total=" + orderPage.getTotal() + ", records=" + orderPage.getRecords().size());
        
        // 转换为DTO
        List<AdminRefundResponseDTO> dtoList = orderPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Page<AdminRefundResponseDTO> result = new Page<>(page, size);
        result.setRecords(dtoList);
        result.setTotal(orderPage.getTotal());
        
        return result;
    }

    /**
     * 批准退票申请
     */
    @Transactional
    public void approveRefund(String orderId) {
        System.out.println("AdminRefundService.approveRefund called with orderId=" + orderId);
        
        // 1. 获取订单信息
        TicketOrder order = ticketOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != TicketOrder.OrderStatus.REFUND_REQUEST) {
            throw new RuntimeException("订单状态不是退票申请中，无法批准退票");
        }
        
        // 2. 更新订单状态为已退款
        order.setStatus(TicketOrder.OrderStatus.REFUNDED);
        order.setRefundTime(LocalDateTime.now());
        order.setRefundAmount(order.getPayAmount()); // 全额退款
        order.setUpdatedAt(LocalDateTime.now());
        
        int updated = ticketOrderMapper.updateById(order);
        if (updated == 0) {
            throw new RuntimeException("更新订单状态失败");
        }
        
        // 3. 给用户余额加钱（如果是余额支付）
        if (order.getPayMethod() == TicketOrder.PaymentMethod.BALANCE) {
            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                user.setBalance(user.getBalance() + order.getPayAmount().doubleValue());
                userMapper.updateById(user);
                System.out.println("已为用户 " + user.getName() + " 退款到余额：" + order.getPayAmount());
            }
        }
        
        System.out.println("退票申请批准成功：" + orderId);
    }

    /**
     * 拒绝退票申请
     */
    @Transactional  
    public void rejectRefund(String orderId, String rejectReason) {
        System.out.println("AdminRefundService.rejectRefund called with orderId=" + orderId + ", reason=" + rejectReason);
        
        // 1. 获取订单信息
        TicketOrder order = ticketOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != TicketOrder.OrderStatus.REFUND_REQUEST) {
            throw new RuntimeException("订单状态不是退票申请中，无法拒绝退票");
        }
        
        // 2. 更新订单状态回到原状态（根据是否已放票）
        // 这里简单设为PAID状态，实际可以根据业务需求调整
        order.setStatus(TicketOrder.OrderStatus.PAID);
        order.setRefundReason(rejectReason); // 将拒绝理由存储在退票理由字段
        order.setUpdatedAt(LocalDateTime.now());
        
        int updated = ticketOrderMapper.updateById(order);
        if (updated == 0) {
            throw new RuntimeException("更新订单状态失败");
        }
        
        System.out.println("退票申请已拒绝：" + orderId + "，理由：" + rejectReason);
    }

    /**
     * 获取退票统计信息
     */
    public Map<String, Object> getRefundStatistics() {
        System.out.println("AdminRefundService.getRefundStatistics called");
        
        Map<String, Object> stats = new HashMap<>();
        
        // 待审核退票数量
        QueryWrapper<TicketOrder> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", TicketOrder.OrderStatus.REFUND_REQUEST);
        Long pendingCount = ticketOrderMapper.selectCount(pendingWrapper);
        
        // 已退款订单数量
        QueryWrapper<TicketOrder> refundedWrapper = new QueryWrapper<>();
        refundedWrapper.eq("status", TicketOrder.OrderStatus.REFUNDED);
        Long refundedCount = ticketOrderMapper.selectCount(refundedWrapper);
        
        // 今日退款金额
        QueryWrapper<TicketOrder> todayWrapper = new QueryWrapper<>();
        todayWrapper.eq("status", TicketOrder.OrderStatus.REFUNDED)
                   .ge("refund_time", LocalDateTime.now().toLocalDate());
        List<TicketOrder> todayRefunds = ticketOrderMapper.selectList(todayWrapper);
        BigDecimal todayAmount = todayRefunds.stream()
                .map(TicketOrder::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("pendingCount", pendingCount);
        stats.put("refundedCount", refundedCount);
        stats.put("todayRefundAmount", todayAmount);
        
        System.out.println("Refund statistics: " + stats);
        return stats;
    }

    /**
     * 转换为DTO
     */
    private AdminRefundResponseDTO convertToDTO(TicketOrder order) {
        AdminRefundResponseDTO dto = new AdminRefundResponseDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setAttractionId(order.getAttractionId());
        dto.setVisitDate(order.getVisitDate());
        dto.setQuantity(order.getQuantity());
        dto.setUnitPrice(order.getUnitPrice());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setContactName(order.getContactName());
        dto.setContactPhone(order.getContactPhone());
        dto.setContactIdcard(order.getContactIdcard());
        dto.setAddress(order.getAddress());
        dto.setStatus(order.getStatus().name());
        dto.setPayMethod(order.getPayMethod().name());
        dto.setPayTime(order.getPayTime());
        dto.setPayAmount(order.getPayAmount());
        dto.setRefundReason(order.getRefundReason());
        dto.setRefundTime(order.getRefundTime());
        dto.setRefundAmount(order.getRefundAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        // 设置状态名称
        dto.setStatusName(getStatusName(order.getStatus()));
        dto.setPayMethodName(getPayMethodName(order.getPayMethod()));
        
        // 获取用户名称
        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            dto.setUserName(user.getName());
        }
        
        // 获取景点名称
        Attraction attraction = attractionMapper.selectById(order.getAttractionId());
        if (attraction != null) {
            dto.setAttractionName(attraction.getName());
        }
        
        return dto;
    }

    private String getStatusName(TicketOrder.OrderStatus status) {
        switch (status) {
            case PAID: return "已支付";
            case TICKETED: return "已放票";
            case REFUND_REQUEST: return "退票申请中";
            case REFUNDED: return "已退款";
            case CANCELLED: return "已取消";
            default: return status.name();
        }
    }

    private String getPayMethodName(TicketOrder.PaymentMethod method) {
        switch (method) {
            case BALANCE: return "余额支付";
            case ALIPAY: return "支付宝";
            case WECHAT: return "微信支付";
            default: return method.name();
        }
    }
}
