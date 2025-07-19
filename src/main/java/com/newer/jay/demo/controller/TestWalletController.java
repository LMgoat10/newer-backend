package com.newer.jay.demo.controller;

import com.newer.jay.demo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestWalletController {
    
    @Autowired
    private WalletService walletService;
    
    /**
     * 测试获取余额（不需要JWT认证）
     */
    @GetMapping("/balance/{userId}")
    public ResponseEntity<Map<String, Object>> testGetBalance(@PathVariable Long userId) {
        try {
            System.out.println("测试获取用户余额, userId: " + userId);
            
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
            System.err.println("获取余额失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "获取余额失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 测试充值（不需要JWT认证）
     */
    @PostMapping("/recharge/{userId}")
    public ResponseEntity<Map<String, Object>> testRecharge(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {
        try {
            System.out.println("测试充值, userId: " + userId + ", amount: " + amount);
            
            // 直接增加余额
            boolean success = walletService.increaseBalance(userId, amount);
            
            if (success) {
                BigDecimal newBalance = walletService.getUserBalance(userId);
                
                Map<String, Object> data = new HashMap<>();
                data.put("userId", userId);
                data.put("amount", amount);
                data.put("newBalance", newBalance);
                
                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("message", "充值成功");
                response.put("data", data);
                
                return ResponseEntity.ok(response);
            } else {
                throw new RuntimeException("充值操作失败");
            }
            
        } catch (Exception e) {
            System.err.println("充值失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "充值失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 检查数据库连接和用户是否存在
     */
    @GetMapping("/check/{userId}")
    public ResponseEntity<Map<String, Object>> checkUser(@PathVariable Long userId) {
        try {
            System.out.println("检查用户, userId: " + userId);
            
            BigDecimal balance = walletService.getUserBalance(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "用户存在，余额: " + balance);
            response.put("userId", userId);
            response.put("balance", balance);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("用户检查失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "用户检查失败: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
