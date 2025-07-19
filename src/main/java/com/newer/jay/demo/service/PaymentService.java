package com.newer.jay.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newer.jay.demo.entity.Payment;
import com.newer.jay.demo.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 处理支付订单
     */
    @Transactional
    public Map<String, Object> processPayment(Long userId, String orderId, String paymentMethodStr) {
        try {
            // 验证订单
            BigDecimal orderAmount = orderService.getOrderAmount(orderId);
            if (orderAmount == null) {
                throw new RuntimeException("订单不存在或已失效");
            }
            
            // 检查订单是否已支付
            if (orderService.isOrderPaid(orderId)) {
                throw new RuntimeException("订单已支付，请勿重复支付");
            }
            
            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(paymentMethodStr.toUpperCase());
            
            // 生成支付ID
            String paymentId = generatePaymentId();
            
            // 创建支付记录
            Payment payment = new Payment(paymentId, orderId, userId, orderAmount, paymentMethod);
            
            // 根据支付方式处理
            switch (paymentMethod) {
                case BALANCE:
                    return processBalancePayment(payment);
                case ALIPAY:
                case WECHAT:
                    return processThirdPartyPayment(payment);
                default:
                    throw new RuntimeException("不支持的支付方式");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("支付失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理余额支付
     */
    private Map<String, Object> processBalancePayment(Payment payment) {
        // 检查余额是否充足
        if (!walletService.hasEnoughBalance(payment.getUserId(), payment.getAmount())) {
            throw new RuntimeException("余额不足");
        }
        
        // 扣除余额
        walletService.decreaseBalance(payment.getUserId(), payment.getAmount());
        
        // 更新支付状态
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        paymentMapper.insert(payment);
        
        // 更新订单状态
        orderService.updateOrderStatus(payment.getOrderId(), "PAID");
        
        // 获取剩余余额
        BigDecimal remainingBalance = walletService.getUserBalance(payment.getUserId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", payment.getOrderId());
        result.put("paymentId", payment.getPaymentId());
        result.put("remainingBalance", remainingBalance);
        
        return result;
    }
    
    /**
     * 处理第三方支付
     */
    private Map<String, Object> processThirdPartyPayment(Payment payment) {
        // 这里应该调用第三方支付接口，这里简化处理
        // 实际应用中需要对接支付宝、微信等支付接口
        
        // 模拟第三方支付成功
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        paymentMapper.insert(payment);
        
        // 更新订单状态
        orderService.updateOrderStatus(payment.getOrderId(), "PAID");
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", payment.getOrderId());
        result.put("paymentId", payment.getPaymentId());
        result.put("remainingBalance", null); // 第三方支付不返回余额
        
        return result;
    }
    
    /**
     * 生成支付ID
     */
    private String generatePaymentId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int)(Math.random() * 1000));
        return "PAY_" + timestamp + "_" + String.format("%03d", Integer.parseInt(random));
    }
    
    /**
     * 查询支付记录
     */
    public Payment getPaymentByOrderId(String orderId) {
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return paymentMapper.selectOne(queryWrapper);
    }
}
