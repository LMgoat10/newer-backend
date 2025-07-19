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
}
