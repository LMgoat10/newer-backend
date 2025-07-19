package com.newer.jay.demo.controller.admin;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.newer.jay.demo.dto.AdminUserResponseDTO;
import com.newer.jay.demo.dto.AdminUserCreateDTO;
import com.newer.jay.demo.dto.AdminUserUpdateDTO;
import com.newer.jay.demo.service.admin.AdminUserService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminUserController {
    
    @Autowired
    private AdminUserService adminUserService;

    /**
     * 获取用户列表（分页）
     */
    @GetMapping("/users")
    public AdminUserResponseDTO getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            HttpServletResponse response) {
        try {
            System.out.println("AdminUserController.getUsers called with page=" + page + ", size=" + size + ", keyword=" + keyword);
            AdminUserResponseDTO result = adminUserService.getUserList(page, size, keyword);
            System.out.println("Service returned: " + result.getStatus() + " - " + result.getMessage());
            return result;
        } catch (Exception e) {
            System.err.println("Error in getUsers: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(500);
            return new AdminUserResponseDTO(500, "服务器内部错误: " + e.getMessage(), null);
        }
    }

    /**
     * 添加用户
     */
    @PostMapping("/users")
    public AdminUserResponseDTO addUser(
            @RequestBody AdminUserCreateDTO createDTO,
            HttpServletResponse response) {
        try {
            return adminUserService.createUser(createDTO);
        } catch (Exception e) {
            response.setStatus(400);
            return new AdminUserResponseDTO(400, "创建用户失败: " + e.getMessage(), null);
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/users/{userId}")
    public AdminUserResponseDTO updateUser(
            @PathVariable Long userId,
            @RequestBody AdminUserUpdateDTO updateDTO,
            HttpServletResponse response) {
        try {
            return adminUserService.updateUser(userId, updateDTO);
        } catch (Exception e) {
            response.setStatus(400);
            return new AdminUserResponseDTO(400, "更新用户失败: " + e.getMessage(), null);
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{userId}")
    public AdminUserResponseDTO deleteUser(
            @PathVariable Long userId,
            HttpServletResponse response) {
        try {
            return adminUserService.deleteUser(userId);
        } catch (Exception e) {
            response.setStatus(400);
            return new AdminUserResponseDTO(400, "删除用户失败: " + e.getMessage(), null);
        }
    }
}
