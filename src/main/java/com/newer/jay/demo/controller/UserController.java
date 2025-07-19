package com.newer.jay.demo.controller;

import com.newer.jay.demo.dto.ApiResponse;
import com.newer.jay.demo.dto.UserUpdateDTO;
import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(@RequestBody UserUpdateDTO dto) {
        log.info("收到用户信息修改请求: userId={}, dto={}", dto.getUserId(), dto);
        try {
            if (dto.getUserId() == null) {
                log.error("用户ID为空，请求体内容: {}", dto);
                return ResponseEntity.ok(ApiResponse.error("用户ID不能为空"));
            }
            User updated = userService.updateProfile(dto);
            return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
        } catch (Exception e) {
            log.error("用户信息修改失败: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
