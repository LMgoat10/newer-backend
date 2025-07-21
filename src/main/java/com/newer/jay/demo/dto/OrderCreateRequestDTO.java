package com.newer.jay.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderCreateRequestDTO {
    private Integer userId;              // 用户ID (JWT认证时可以从token获取)
    private Long attractionId;           // 景点ID
    private LocalDate visitDate;         // 游玩日期
    private Integer quantity;            // 门票数量
    private BigDecimal unitPrice;        // 单价
    private BigDecimal totalAmount;      // 总金额
    
    // 联系人信息
    private String contactName;          // 联系人姓名
    private String contactPhone;         // 联系人电话
    private String contactIdcard;        // 联系人身份证号
    private String contactEmail;         // 联系人邮箱
    private String address;              // 收货地址
    
    private String payMethod;            // 支付方式：BALANCE, ALIPAY, WECHAT
    private List<Long> cartItemIds;      // 购物车项ID列表（可选）
}
