package com.newer.jay.demo.service.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.AdminUserResponseDTO;
import com.newer.jay.demo.dto.AdminUserCreateDTO;
import com.newer.jay.demo.dto.AdminUserUpdateDTO;
import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    @Resource
    private UserMapper userMapper;
    
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 获取用户列表（分页）
     */
    public AdminUserResponseDTO getUserList(Integer page, Integer size, String keyword) {
        try {
            System.out.println("AdminUserService.getUserList called with page=" + page + ", size=" + size + ", keyword=" + keyword);
            
            Page<User> userPage = new Page<>(page, size);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            
            // 搜索条件
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.and(wrapper -> wrapper
                    .like("name", keyword)
                    .or()
                    .like("email", keyword)
                    .or()
                    .like("phone", keyword)
                );
                System.out.println("Added search keyword: " + keyword);
            }
            
            // 按创建时间降序排列 - 使用数据库字段名
            queryWrapper.orderByDesc("join_date");
            
            System.out.println("Executing query...");
            Page<User> result = userMapper.selectPage(userPage, queryWrapper);
            System.out.println("Query result: total=" + result.getTotal() + ", records=" + result.getRecords().size());
            
            // 转换为DTO
            List<AdminUserResponseDTO.AdminUserDTO> userDTOs = result.getRecords().stream()
                .map(user -> new AdminUserResponseDTO.AdminUserDTO(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getBalance() != null ? user.getBalance() : 0.0,
                    "ACTIVE", // 默认状态，可以根据需要添加状态字段
                    formatDate(user.getJoinDate()),
                    user.getTotalOrders() != null ? user.getTotalOrders() : 0,
                    "USER" // 默认角色，可以根据需要添加角色字段
                ))
                .collect(Collectors.toList());
            
            AdminUserResponseDTO.AdminUserPageData pageData = 
                new AdminUserResponseDTO.AdminUserPageData(result.getTotal(), userDTOs);
            
            return new AdminUserResponseDTO(200, "获取用户列表成功", pageData);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminUserResponseDTO(500, "获取用户列表失败: " + e.getMessage(), null);
        }
    }

    /**
     * 创建用户
     */
    public AdminUserResponseDTO createUser(AdminUserCreateDTO createDTO) {
        try {
            // 检查邮箱是否已存在
            QueryWrapper<User> emailQuery = new QueryWrapper<>();
            emailQuery.eq("email", createDTO.getEmail());
            if (userMapper.selectCount(emailQuery) > 0) {
                return new AdminUserResponseDTO(400, "邮箱已存在", null);
            }

            // 检查手机号是否已存在（如果提供）
            if (createDTO.getPhone() != null && !createDTO.getPhone().isEmpty()) {
                QueryWrapper<User> phoneQuery = new QueryWrapper<>();
                phoneQuery.eq("phone", createDTO.getPhone());
                if (userMapper.selectCount(phoneQuery) > 0) {
                    return new AdminUserResponseDTO(400, "手机号已存在", null);
                }
            }

            // 创建新用户
            User user = new User();
            user.setName(createDTO.getName());
            user.setEmail(createDTO.getEmail());
            user.setPhone(createDTO.getPhone());
            user.setHashedPassword(passwordEncoder.encode(createDTO.getPassword()));
            user.setMemberLevel("BRONZE");
            user.setJoinDate(new Date());
            user.setTotalOrders(0);
            user.setPoints(0);
            user.setBalance(0.0);

            int result = userMapper.insert(user);
            
            if (result > 0) {
                return new AdminUserResponseDTO(200, "用户创建成功", null);
            } else {
                return new AdminUserResponseDTO(500, "用户创建失败", null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminUserResponseDTO(500, "用户创建失败: " + e.getMessage(), null);
        }
    }

    /**
     * 更新用户
     */
    public AdminUserResponseDTO updateUser(Long userId, AdminUserUpdateDTO updateDTO) {
        try {
            User existingUser = userMapper.selectById(userId);
            if (existingUser == null) {
                return new AdminUserResponseDTO(404, "用户不存在", null);
            }

            // 检查邮箱是否被其他用户使用
            if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(existingUser.getEmail())) {
                QueryWrapper<User> emailQuery = new QueryWrapper<>();
                emailQuery.eq("email", updateDTO.getEmail()).ne("userId", userId);
                if (userMapper.selectCount(emailQuery) > 0) {
                    return new AdminUserResponseDTO(400, "邮箱已被其他用户使用", null);
                }
            }

            // 更新用户信息
            User updateUser = new User();
            updateUser.setUserId(userId);
            
            if (updateDTO.getName() != null && !updateDTO.getName().isEmpty()) {
                updateUser.setName(updateDTO.getName());
            }
            if (updateDTO.getEmail() != null && !updateDTO.getEmail().isEmpty()) {
                updateUser.setEmail(updateDTO.getEmail());
            }
            if (updateDTO.getPhone() != null && !updateDTO.getPhone().isEmpty()) {
                updateUser.setPhone(updateDTO.getPhone());
            }
            // 这里可以添加状态字段的更新逻辑

            int result = userMapper.updateById(updateUser);
            
            if (result > 0) {
                return new AdminUserResponseDTO(200, "用户更新成功", null);
            } else {
                return new AdminUserResponseDTO(500, "用户更新失败", null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminUserResponseDTO(500, "用户更新失败: " + e.getMessage(), null);
        }
    }

    /**
     * 删除用户
     */
    public AdminUserResponseDTO deleteUser(Long userId) {
        try {
            User existingUser = userMapper.selectById(userId);
            if (existingUser == null) {
                return new AdminUserResponseDTO(404, "用户不存在", null);
            }

            int result = userMapper.deleteById(userId);
            
            if (result > 0) {
                return new AdminUserResponseDTO(200, "用户删除成功", null);
            } else {
                return new AdminUserResponseDTO(500, "用户删除失败", null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminUserResponseDTO(500, "用户删除失败: " + e.getMessage(), null);
        }
    }

    /**
     * 格式化日期
     */
    private String formatDate(Date date) {
        if (date == null) return null;
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
