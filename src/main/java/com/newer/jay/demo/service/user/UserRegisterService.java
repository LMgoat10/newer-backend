package com.newer.jay.demo.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newer.jay.demo.mapper.UserMapper;
import com.newer.jay.demo.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Service
public class UserRegisterService {
    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册服务
     * @param name 用户名
     * @param email 邮箱
     * @param password 密码
     * @param phone 手机号码（可选）
     * @param avatarFileName 头像文件名（可选）
     * @return 注册结果
     */
    public Map<String, Object> register(String name, String email, String password, String phone, String avatarFileName) {
        try {
            // 验证输入参数
            if (name == null || name.trim().isEmpty()) {
                return Map.of("status", 1, "message", "用户名不能为空");
            }
            if (email == null || email.trim().isEmpty()) {
                return Map.of("status", 1, "message", "邮箱不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                return Map.of("status", 1, "message", "密码不能为空");
            }
            
            // 验证邮箱格式
            if (!isValidEmail(email)) {
                return Map.of("status", 1, "message", "邮箱格式不正确");
            }
            
            // 验证密码强度
            if (password.length() < 6) {
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

            // 加密密码
            String encodedPassword = passwordEncoder.encode(password);

            // 创建新用户
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setHashedPassword(encodedPassword);
            user.setAvatarFileName(avatarFileName);
            user.setMemberLevel("普通会员");
            user.setJoinDate(new Date());
            user.setTotalOrders(0);
            user.setBalance(0.0);

            // 保存用户
            int result = userMapper.insert(user);
            
            if (result > 0) {
                return Map.of(
                    "status", 0, 
                    "message", "注册成功", 
                    "userId", user.getUserId(),
                    "name", user.getName(),
                    "email", user.getEmail()
                );
            } else {
                return Map.of("status", 1, "message", "注册失败，请稍后重试");
            }

        } catch (Exception e) {
            // 记录错误日志
            System.err.println("用户注册异常: " + e.getMessage());
            e.printStackTrace();
            return Map.of("status", 1, "message", "系统异常，注册失败");
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
