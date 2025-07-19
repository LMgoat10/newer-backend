package com.newer.jay.demo.service;

import com.newer.jay.demo.dto.UserUpdateDTO;
import com.newer.jay.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private com.newer.jay.demo.mapper.UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public User updateProfile(UserUpdateDTO dto) {
        log.debug("开始更新用户信息: userId={}", dto.getUserId());
        
        // 1. 验证用户是否存在
        User user = validateUser(dto.getUserId());
        
        try {
            // 2. 处理密码更新
            if (isPasswordUpdateRequested(dto)) {
                updatePassword(user, dto.getOldPassword(), dto.getPassword());
            }
            
            // 3. 更新基本信息
            updateBasicInfo(user, dto);
            
            // 4. 保存更新（使用where条件确保更新正确的记录）
            int rows = userMapper.updateById(user);
            if (rows != 1) {
                log.error("用户信息更新失败，userId: {}, 影响行数: {}", dto.getUserId(), rows);
                throw new RuntimeException("更新失败，请稍后重试");
            }
            
            log.debug("用户信息更新成功: userId={}", dto.getUserId());
            User updatedUser = userMapper.selectById(dto.getUserId());
            if (updatedUser == null) {
                throw new RuntimeException("获取更新后的用户信息失败");
            }
            return updatedUser;
            
        } catch (Exception e) {
            log.error("更新用户信息时发生错误: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private User validateUser(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }
    
    private boolean isPasswordUpdateRequested(UserUpdateDTO dto) {
        return dto.getPassword() != null && !dto.getPassword().trim().isEmpty();
    }
    
    private void updatePassword(User user, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new RuntimeException("请输入原密码");
        }
        
        // 使用PasswordEncoder验证密码
        if (!passwordEncoder.matches(oldPassword, user.getHashedPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 使用PasswordEncoder加密新密码
        user.setHashedPassword(passwordEncoder.encode(newPassword));
    }
    
    private void updateBasicInfo(User user, UserUpdateDTO dto) {
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            user.setPhone(dto.getPhone());
        }
    }

    // 不再需要hash方法，已使用Spring Security的PasswordEncoder
}
