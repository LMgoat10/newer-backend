package com.newer.jay.demo.controller.user;

import com.newer.jay.demo.dto.LoginResponseDTO;
import com.newer.jay.demo.dto.UserRegisterDTO;
import com.newer.jay.demo.service.user.UserService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 用户控制器 - 整合所有用户相关的接口
 * 包括：用户信息、登录、注册、更新、头像上传
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取当前用户信息
     * @param response HTTP响应
     * @return 用户信息
     */
    @GetMapping("/auth/me")
    public LoginResponseDTO getUserInfo(HttpServletResponse response) {
        try {
            return userService.getUserInfo();
        } catch (Exception e) {
            response.setStatus(500);
            return new LoginResponseDTO(1, "服务器内部错误: " + e.getMessage(), null, null);
        }
    }

    /**
     * 用户登录
     * @param map 登录信息（email, password）
     * @param response HTTP响应
     * @return 登录结果
     */
    @PostMapping("/auth/login")
    public LoginResponseDTO login(@RequestBody Map<String, Object> map, HttpServletResponse response) {
        String email = (String) map.get("email");
        String password = (String) map.get("password");

        if (email == null || password == null) {
            response.setStatus(400);
            return new LoginResponseDTO(1, "email and password are required", null, null);
        }

        try {
            return userService.login(email, password);
        } catch (Exception e) {
            response.setStatus(500);
            return new LoginResponseDTO(1, e.getMessage(), null, null);
        }
    }

    /**
     * 用户注册接口
     * @param registerDTO 注册信息
     * @param response HTTP响应
     * @return 注册结果
     */
    @PostMapping("/auth/register")
    public Map<String, Object> register(@RequestBody UserRegisterDTO registerDTO, HttpServletResponse response) {
        try {
            Map<String, Object> result = userService.register(
                registerDTO.getName(), 
                registerDTO.getEmail(), 
                registerDTO.getPassword(), 
                registerDTO.getPhone(), 
                registerDTO.getAvatarFileName()
            );
            
            int status = (Integer) result.get("status");
            if (status != 0) {
                response.setStatus(400);
            }
            response.setStatus(200);
            return result;
        } catch (Exception e) {
            response.setStatus(500);
            return Map.of("status", 1, "message", "服务器内部错误: " + e.getMessage());
        }
    }

    /**
     * 用户注册接口（Map方式，兼容性）
     * @param requestBody 注册信息
     * @param response HTTP响应
     * @return 注册结果
     */
    @PostMapping("/auth/register-map")
    public Map<String, Object> registerWithMap(@RequestBody Map<String, Object> requestBody, HttpServletResponse response) {
        try {
            String name = (String) requestBody.get("name");
            String email = (String) requestBody.get("email");
            String password = (String) requestBody.get("password");
            String phone = (String) requestBody.get("phone");
            String avatarFileName = (String) requestBody.get("avatarFileName");

            Map<String, Object> result = userService.register(name, email, password, phone, avatarFileName);
            
            int status = (Integer) result.get("status");
            if (status != 0) {
                response.setStatus(400);
            }
            
            return result;
        } catch (Exception e) {
            response.setStatus(500);
            return Map.of("status", 1, "message", "服务器内部错误: " + e.getMessage());
        }
    }

    /**
     * 检查用户名是否可用
     * @param name 用户名
     * @return 检查结果
     */
    @GetMapping("/auth/check-username")
    public Map<String, Object> checkUsername(@RequestParam String name) {
        try {
            boolean available = userService.isUsernameAvailable(name);
            return Map.of(
                "status", 0,
                "available", available,
                "message", available ? "用户名可用" : "用户名已被占用"
            );
        } catch (Exception e) {
            return Map.of("status", 1, "message", "检查失败: " + e.getMessage());
        }
    }

    /**
     * 检查邮箱是否可用
     * @param email 邮箱
     * @return 检查结果
     */
    @GetMapping("/auth/check-email")
    public Map<String, Object> checkEmail(@RequestParam String email) {
        try {
            boolean available = userService.isEmailAvailable(email);
            return Map.of(
                "status", 0,
                "available", available,
                "message", available ? "邮箱可用" : "邮箱已被注册"
            );
        } catch (Exception e) {
            return Map.of("status", 1, "message", "检查失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * @param userMap 用户信息
     * @return 更新结果
     */
    @PostMapping("/user/update")
    public Map<String, Object> updateUser(@RequestBody Map<String, Object> userMap) {
        try {
            return userService.updateUser(
                (String) userMap.get("name"),
                (String) userMap.get("email"),
                (String) userMap.get("password"),
                (String) userMap.get("phone"),
                (String) userMap.get("avatarFileName")
            );
        } catch (Exception e) {
            return Map.of("code", 500, "message", "服务器内部错误: " + e.getMessage());
        }
    }

    // ==================== 文件上传相关接口 ====================

    /**
     * 上传用户头像
     * @param avatar 头像文件
     * @param response HTTP响应
     * @return 上传结果
     */
    @PostMapping("/upload/avatar")
    public Map<String, Object> uploadAvatar(@RequestParam("avatar") MultipartFile avatar, HttpServletResponse response) {
        if (avatar == null || avatar.isEmpty()) {
            response.setStatus(400);
            return Map.of("status", 1, "message", "请选择要上传的头像文件");
        }

        try {
            return userService.uploadAvatar(avatar);
        } catch (Exception e) {
            response.setStatus(500);
            return Map.of("status", 1, "message", "上传失败: " + e.getMessage());
        }
    }
}
