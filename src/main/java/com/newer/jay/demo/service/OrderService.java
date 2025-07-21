package com.newer.jay.demo.service;

import com.newer.jay.demo.dto.*;
import com.newer.jay.demo.entity.Attraction;
import com.newer.jay.demo.entity.TicketOrder;
import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.mapper.AttractionMapper;
import com.newer.jay.demo.mapper.TicketOrderMapper;
import com.newer.jay.demo.mapper.UserMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private TicketOrderMapper ticketOrderMapper;

    @Autowired
    private AttractionMapper attractionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建订单
     */
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateRequestDTO requestDTO) {
        // 1. 验证用户
        User user = userMapper.selectById(requestDTO.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 验证景点并检查库存
        Attraction attraction = attractionMapper.selectById(requestDTO.getAttractionId());
        if (attraction == null) {
            throw new RuntimeException("景点不存在");
        }
        if (attraction.getAvailableTickets() < requestDTO.getQuantity()) {
            throw new RuntimeException("库存不足");
        }

        // 3. 计算总价
        BigDecimal totalAmount = attraction.getPrice().multiply(new BigDecimal(requestDTO.getQuantity()));

        // 4. 生成订单号
        String orderNumber = generateOrderNumber();

        // 5. 创建订单
        TicketOrder order = new TicketOrder(
            requestDTO.getUserId(),
            requestDTO.getAttractionId(),
            orderNumber,
            requestDTO.getQuantity(),
            attraction.getPrice(),
            totalAmount,
            requestDTO.getVisitDate(),
            requestDTO.getContactName(),
            requestDTO.getContactPhone()
        );

        // 6. 保存订单
        ticketOrderMapper.insert(order);

        // 7. 更新景点库存
        attraction.setAvailableTickets(attraction.getAvailableTickets() - requestDTO.getQuantity());
        attractionMapper.updateById(attraction);

        // 8. 返回订单信息
        return convertToOrderResponseDTO(order, attraction);
    }

    /**
     * 获取用户订单列表（分页）
     */
    public PageResponse<OrderResponseDTO> getUserOrders(Integer userId, Integer page, Integer size, String status) {
        Page<TicketOrder> orderPage = new Page<>(page, size);
        IPage<TicketOrder> result = ticketOrderMapper.selectUserOrdersPage(orderPage, userId, status, null, null);
        
        List<OrderResponseDTO> orderDTOs = result.getRecords().stream()
            .map(order -> {
                Attraction attraction = attractionMapper.selectById(order.getAttractionId());
                return convertToOrderResponseDTO(order, attraction);
            })
            .collect(Collectors.toList());

        return new PageResponse<>(orderDTOs, page, size, result.getTotal());
    }

    /**
     * 获取订单详情
     */
    public OrderResponseDTO getOrderDetails(String orderNumber) {
        TicketOrder order = ticketOrderMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TicketOrder>()
                .eq("id", orderNumber)
        );
        
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        Attraction attraction = attractionMapper.selectById(order.getAttractionId());
        return convertToOrderResponseDTO(order, attraction);
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(String orderNumber, Integer userId) {
        TicketOrder order = ticketOrderMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TicketOrder>()
                .eq("id", orderNumber)
                .eq("user_id", userId)
        );
        
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() != TicketOrder.OrderStatus.PAID) {
            throw new RuntimeException("只能取消已支付的订单");
        }

        // 更新订单状态
        order.setStatus(TicketOrder.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        ticketOrderMapper.updateById(order);

        // 恢复库存
        Attraction attraction = attractionMapper.selectById(order.getAttractionId());
        attraction.setAvailableTickets(attraction.getAvailableTickets() + order.getQuantity());
        attractionMapper.updateById(attraction);
    }

    /**
     * 申请退款
     */
    @Transactional
    public void requestRefund(String orderNumber, RefundRequestDTO requestDTO) {
        TicketOrder order = ticketOrderMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TicketOrder>()
                .eq("id", orderNumber)
                .eq("user_id", requestDTO.getUserId())
        );
        
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() != TicketOrder.OrderStatus.PAID) {
            throw new RuntimeException("只能对已支付的订单申请退款");
        }

        // 更新订单状态
        order.setStatus(TicketOrder.OrderStatus.REFUND_REQUEST);
        order.setRefundReason(requestDTO.getRefundReason());
        order.setUpdatedAt(LocalDateTime.now());
        ticketOrderMapper.updateById(order);
    }

    /**
     * 获取用户订单统计
     */
    public List<Map<String, Object>> getUserOrderStats(Integer userId) {
        return ticketOrderMapper.getUserOrderStats(userId);
    }

    /**
     * 模拟支付订单（在实际应用中，这会与支付网关集成）
     */
    @Transactional
    public void processPayment(String orderNumber, TicketOrder.PayMethod payMethod) {
        TicketOrder order = ticketOrderMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TicketOrder>()
                .eq("order_number", orderNumber)
        );
        
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() == TicketOrder.OrderStatus.PAID) {
            throw new RuntimeException("订单已支付");
        }

        // 模拟支付处理
        order.setStatus(TicketOrder.OrderStatus.PAID);
        order.setPayMethod(payMethod);
        order.setPaidAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        ticketOrderMapper.updateById(order);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        return "ORDER_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
               + "_" + System.nanoTime() % 1000;
    }

    /**
     * 转换为订单响应DTO
     */
    private OrderResponseDTO convertToOrderResponseDTO(TicketOrder order, Attraction attraction) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUserId());
        dto.setAttractionId(order.getAttractionId());
        dto.setAttractionName(attraction != null ? attraction.getName() : "未知景点");
        dto.setQuantity(order.getQuantity());
        dto.setUnitPrice(order.getUnitPrice());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setVisitDate(order.getVisitDate());
        dto.setContactName(order.getContactName());
        dto.setContactPhone(order.getContactPhone());
        // dto.setContactEmail(order.getContactEmail()); // 数据库中没有此字段，暂时注释
        dto.setStatus(order.getStatus().name());
        dto.setPayMethod(order.getPayMethod() != null ? order.getPayMethod().name() : null);
        dto.setRefundReason(order.getRefundReason());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPaidAt(order.getPaidAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    // ========== 兼容性方法（为其他服务提供支持） ==========

    /**
     * 获取订单金额（兼容旧代码）
     */
    public BigDecimal getOrderAmount(String orderId) {
        TicketOrder order = ticketOrderMapper.selectById(orderId);
        return order != null ? order.getTotalAmount() : null;
    }

    /**
     * 检查订单是否已支付（兼容旧代码）
     */
    public boolean isOrderPaid(String orderId) {
        TicketOrder order = ticketOrderMapper.selectById(orderId);
        return order != null && (order.getStatus() == TicketOrder.OrderStatus.PAID || 
                                order.getStatus() == TicketOrder.OrderStatus.TICKETED);
    }

    /**
     * 更新订单状态（兼容旧代码）
     */
    public void updateOrderStatus(String orderId, String status) {
        TicketOrder order = ticketOrderMapper.selectById(orderId);
        if (order != null) {
            try {
                order.setStatus(TicketOrder.OrderStatus.valueOf(status));
                order.setUpdatedAt(LocalDateTime.now());
                ticketOrderMapper.updateById(order);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的订单状态: " + status);
            }
        }
    }

    /**
     * 获取订单信息（兼容旧代码）
     * 注意：这个方法返回一个简化的订单信息对象
     */
    public OrderInfo getOrderInfo(String orderId) {
        TicketOrder order = ticketOrderMapper.selectById(orderId);
        if (order != null) {
            return new OrderInfo(order.getId(), order.getTotalAmount(), order.getStatus().name());
        }
        return null;
    }

    /**
     * 兼容性内部类，用于支持旧代码
     */
    public static class OrderInfo {
        private String orderId;
        private BigDecimal amount;
        private String status;
        
        public OrderInfo(String orderId, BigDecimal amount, String status) {
            this.orderId = orderId;
            this.amount = amount;
            this.status = status;
        }
        
        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
