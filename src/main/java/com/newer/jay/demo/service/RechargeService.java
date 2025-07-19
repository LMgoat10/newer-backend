package com.newer.jay.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class RechargeService {
    
    @Autowired
    private WalletService walletService;
    
    /**
     * 处理充值
     */
    @Transactional
    public Map<String, Object> processRecharge(Long userId, BigDecimal amount, String paymentMethodStr) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("充值金额必须大于0");
            }
            
            // 生成充值ID（用于记录和追踪，不存数据库）
            String rechargeId = generateRechargeId();
            
            // 解析支付方式
            String paymentMethod = paymentMethodStr.toUpperCase();
            
            // 模拟第三方支付成功（实际应用中需要对接真实支付接口）
            if ("ALIPAY".equals(paymentMethod) || "WECHAT".equals(paymentMethod) || "BALANCE".equals(paymentMethod)) {
                // 这里应该调用第三方支付接口
                // 模拟支付成功，直接增加钱包余额
                walletService.increaseBalance(userId, amount);
            } else {
                throw new RuntimeException("不支持的充值方式: " + paymentMethod);
            }
            
            // 获取新的余额
            BigDecimal newBalance = walletService.getUserBalance(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("rechargeId", rechargeId);
            result.put("amount", amount);
            result.put("newBalance", newBalance);
            result.put("paymentMethod", paymentMethod);
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("充值失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成充值ID
     */
    private String generateRechargeId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int)(Math.random() * 1000));
        return "RECHARGE_" + timestamp + "_" + String.format("%03d", Integer.parseInt(random));
    }
}
