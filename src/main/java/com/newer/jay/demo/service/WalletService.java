package com.newer.jay.demo.service;

import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取用户余额
     */
    public BigDecimal getUserBalance(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getBalance() != null ? BigDecimal.valueOf(user.getBalance()) : BigDecimal.ZERO;
    }
    
    /**
     * 增加余额
     */
    @Transactional
    public boolean increaseBalance(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("金额必须大于0");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        BigDecimal currentBalance = user.getBalance() != null ? BigDecimal.valueOf(user.getBalance()) : BigDecimal.ZERO;
        BigDecimal newBalance = currentBalance.add(amount);
        
        user.setBalance(newBalance.doubleValue());
        int result = userMapper.updateById(user);
        return result > 0;
    }
    
    /**
     * 减少余额
     */
    @Transactional
    public boolean decreaseBalance(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("金额必须大于0");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        BigDecimal currentBalance = user.getBalance() != null ? BigDecimal.valueOf(user.getBalance()) : BigDecimal.ZERO;
        
        if (currentBalance.compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }
        
        BigDecimal newBalance = currentBalance.subtract(amount);
        user.setBalance(newBalance.doubleValue());
        int result = userMapper.updateById(user);
        return result > 0;
    }
    
    /**
     * 检查余额是否充足
     */
    public boolean hasEnoughBalance(Long userId, BigDecimal amount) {
        BigDecimal balance = getUserBalance(userId);
        return balance.compareTo(amount) >= 0;
    }
    
    /**
     * 扣除用户余额（用于订单支付）
     * @param userId 用户ID
     * @param amount 扣除金额
     * @param orderId 订单ID
     * @param description 扣款描述
     * @return 扣款后的新余额
     */
    @Transactional
    public BigDecimal deductBalance(Long userId, BigDecimal amount, String orderId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("扣除金额必须大于0");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        BigDecimal currentBalance = user.getBalance() != null ? BigDecimal.valueOf(user.getBalance()) : BigDecimal.ZERO;
        
        if (currentBalance.compareTo(amount) < 0) {
            throw new RuntimeException("余额不足，当前余额: " + currentBalance + "，需要扣除: " + amount);
        }
        
        BigDecimal newBalance = currentBalance.subtract(amount);
        user.setBalance(newBalance.doubleValue());
        int result = userMapper.updateById(user);
        
        if (result <= 0) {
            throw new RuntimeException("余额扣除失败");
        }
        
        // 可以在这里记录扣款日志，比如插入到钱包交易记录表
        // TODO: 添加钱包交易记录
        
        return newBalance;
    }
}
