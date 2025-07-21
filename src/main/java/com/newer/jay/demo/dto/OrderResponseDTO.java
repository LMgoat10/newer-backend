package com.newer.jay.demo.dto;

import com.newer.jay.demo.entity.TicketOrder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrderResponseDTO {
    private String orderId;               // 订单ID
    private String orderNumber;           // 订单号
    private Integer userId;               // 用户ID
    private Long attractionId;            // 景点ID
    private String attractionName;        // 景点名称
    private String attractionImage;       // 景点图片
    private LocalDate visitDate;          // 游玩日期
    private Integer quantity;             // 门票数量
    private BigDecimal unitPrice;         // 单价
    private BigDecimal totalAmount;       // 总金额
    
    // 联系人信息
    private String contactName;           // 联系人姓名
    private String contactPhone;         // 联系人电话
    private String contactIdcard;        // 联系人身份证号
    private String contactEmail;         // 联系人邮箱
    private String address;              // 收货地址
    
    // 订单状态
    private String status;               // 订单状态
    
    // 支付信息
    private String payMethod;            // 支付方式
    private LocalDateTime paidAt;        // 支付时间
    private BigDecimal payAmount;        // 支付金额
    
    // 退款信息
    private String refundReason;         // 退款原因
    private LocalDateTime refundTime;    // 退款时间
    private BigDecimal refundAmount;     // 退款金额
    
    private LocalDateTime createdAt;     // 创建时间
    private LocalDateTime updatedAt;     // 更新时间
    
    public static OrderResponseDTO fromEntity(TicketOrder order, String attractionName, String attractionImage) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setUserId(order.getUserId());
        dto.setAttractionId(order.getAttractionId());
        dto.setAttractionName(attractionName);
        dto.setAttractionImage(attractionImage);
        dto.setVisitDate(order.getVisitDate());
        dto.setQuantity(order.getQuantity());
        dto.setUnitPrice(order.getUnitPrice());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setContactName(order.getContactName());
        dto.setContactPhone(order.getContactPhone());
        dto.setContactIdcard(order.getContactIdcard());
        dto.setAddress(order.getAddress());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        dto.setPayMethod(order.getPayMethod() != null ? order.getPayMethod().name() : null);
        dto.setPaidAt(order.getPayTime());
        dto.setPayAmount(order.getPayAmount());
        dto.setRefundReason(order.getRefundReason());
        dto.setRefundTime(order.getRefundTime());
        dto.setRefundAmount(order.getRefundAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }
}
