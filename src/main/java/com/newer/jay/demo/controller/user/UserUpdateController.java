package com.newer.jay.demo.controller.user;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.newer.jay.demo.service.user.UserUpdateService;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class UserUpdateController {
    @Resource
    private UserUpdateService userUpdateService;

    @PostMapping("/update")
    public Map<String, Object> updateUser(@RequestBody Map<String, Object> userMap) {
        try {
            return userUpdateService.updateUser(
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
}
