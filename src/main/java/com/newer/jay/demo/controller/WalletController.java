package com.newer.jay.demo.controller;

import com.newer.jay.demo.dto.PaymentRequest;
import com.newer.jay.demo.dto.RechargeRequest;
import com.newer.jay.demo.dto.RefundRequest;
import com.newer.jay.demo.service.PaymentService;
import com.newer.jay.demo.service.RechargeService;
import com.newer.jay.demo.service.RefundService;
import com.newer.jay.demo.service.WalletService;
import com.newer.jay.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class WalletController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private RechargeService rechargeService;
    
    @Autowired
    private RefundService refundService;
    
    @Autowired
    private WalletService walletService;
    
    /**
     * 5.1 支付订单
     * POST /api/user/payment
     */
    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> processPayment(
            @RequestHeader("Authorization") String authorization,
            @RequestBody PaymentRequest request) {
        
        try {
            // 从JWT token中获取用户ID（这里简化处理，实际应该解析JWT）
            Long userId = extractUserIdFromToken(authorization);
            
            Map<String, Object> result = paymentService.processPayment(
                userId, 
                request.getOrderId(), 
                request.getPaymentMethod()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "支付成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 5.2 账户充值
     * POST /api/user/recharge
     */
    @PostMapping("/recharge")
    public ResponseEntity<Map<String, Object>> processRecharge(
            @RequestHeader("Authorization") String authorization,
            @RequestBody RechargeRequest request) {
        
        try {
            // 从JWT token中获取用户ID
            Long userId = extractUserIdFromToken(authorization);
            
            Map<String, Object> result = rechargeService.processRecharge(
                userId,
                request.getAmount(),
                request.getPaymentMethod()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "充值成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 5.3 申请退票
     * POST /api/user/refund
     */
    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> applyRefund(
            @RequestHeader("Authorization") String authorization,
            @RequestBody RefundRequest request) {
        
        try {
            // 从JWT token中获取用户ID
            Long userId = extractUserIdFromToken(authorization);
            
            Map<String, Object> result = refundService.applyRefund(
                userId,
                request.getOrderId(),
                request.getReason()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "退票申请提交成功，等待管理员审核");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取钱包余额
     * GET /api/user/wallet/balance
     */
    @GetMapping("/wallet/balance")
    public ResponseEntity<Map<String, Object>> getBalance(
            @RequestHeader("Authorization") String authorization) {
        
        try {
            // 从JWT token中获取用户ID
            Long userId = extractUserIdFromToken(authorization);
            
            BigDecimal balance = walletService.getUserBalance(userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("balance", balance);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "获取余额成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 从JWT token中提取用户ID
     */
    private Long extractUserIdFromToken(String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                System.err.println("Authorization header 格式错误: " + authorization);
                throw new RuntimeException("无效的认证信息");
            }
            
            String token = authorization.substring(7); // 去掉 "Bearer "
            System.out.println("正在解析JWT token: " + token.substring(0, Math.min(token.length(), 20)) + "...");
            
            Claims claims = JwtUtil.parseJWT(token);
            String subject = claims.getSubject();
            System.out.println("JWT subject: " + subject);
            
            // 假设subject就是用户ID，根据实际实现调整
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            System.err.println("JWT subject 不是有效的用户ID: " + e.getMessage());
            // 对于测试，返回默认用户ID
            return 1L;
        } catch (Exception e) {
            // JWT解析失败时，返回默认测试用户ID（仅开发环境）
            System.err.println("JWT解析失败，使用默认用户ID: " + e.getMessage());
            e.printStackTrace();
            return 1L;
        }
    }
}
