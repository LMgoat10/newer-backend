package com.newer.jay.demo.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.mapper.UserMapper;

import jakarta.annotation.Resource;

import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserUpdateService {
    @Resource
    private PasswordEncoder passwordEncoder;
    
    @Resource
    private UserMapper userMapper;

    public Map<String, Object> updateUser(String name, String email, String password, String phone, String avatarFileName) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailImpl loginUser = (UserDetailImpl) authenticationToken.getPrincipal();
        try {    
            // 验证邮箱格式
            if (email != null && !email.isEmpty() && !isValidEmail(email)) {
                return Map.of("status", 1, "message", "邮箱格式不正确");
            }
            
            // 验证密码强度
            if (password != null && !password.isEmpty() && password.length() < 6) {
                return Map.of("status", 1, "message", "密码长度至少6位");
            }

            // 检查邮箱是否已存在
            QueryWrapper<User> emailQuery = new QueryWrapper<>();
            emailQuery.eq("email", email);
            if (userMapper.selectCount(emailQuery) > 0) {
                return Map.of("status", 1, "message", "邮箱已被注册");
            }

            // 检查用户名是否已存在
            QueryWrapper<User> nameQuery = new QueryWrapper<>();
            nameQuery.eq("name", name);
            if (userMapper.selectCount(nameQuery) > 0) {
                return Map.of("status", 1, "message", "用户名已被占用");
            }
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", loginUser.getUser().getUserId());
            User user = userMapper.selectOne(queryWrapper);
            if(name != null && !name.isEmpty()) {
                user.setName(name);
            }

            if(email != null && !email.isEmpty()) {
                user.setEmail(email);
            }

            if(phone != null && !phone.isEmpty()) {
                user.setPhone(phone);
            }

            if(password != null && !password.isEmpty()) {
                user.setHashedPassword(passwordEncoder.encode(password));
            }

            if(avatarFileName != null && !avatarFileName.isEmpty()) {
                user.setAvatarFileName(avatarFileName);
            }
            
            if (userMapper.updateById(user) > 0) {
                return Map.of(
                    "status", 0, 
                    "message", "更新成功", 
                    "userId", user.getUserId(),
                    "name", user.getName(),
                    "email", user.getEmail()
                );
            } else {
                return Map.of("status", 1, "message", "更新失败，请稍后重试");
            }

        } catch (Exception e) {
            // 记录错误日志
            System.err.println("用户更新异常: " + e.getMessage());
            e.printStackTrace();
            return Map.of("status", 1, "message", "系统异常，更新失败");
        }
    }
    /**
     * 验证邮箱格式
     * @param email 邮箱地址
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * 检查用户名是否可用
     * @param name 用户名
     * @return 是否可用
     */
    public boolean isUsernameAvailable(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("name", name);
        return userMapper.selectCount(query) == 0;
    }

    /**
     * 检查邮箱是否可用
     * @param email 邮箱
     * @return 是否可用
     */
    public boolean isEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email", email);
        return userMapper.selectCount(query) == 0;
    }
}