package com.newer.jay.demo.controller.user;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.mapper.UserMapper;
import com.newer.jay.demo.service.user.UserDetailImpl;
import com.newer.jay.demo.service.user.UserUpdateService;
import com.newer.jay.demo.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;

@RestController
@RequestMapping("/api/user")
public class UserUpdateController {
    @Resource
    private UserUpdateService userUpdateService;

    @Resource
    private UserMapper userMapper;

    @PostMapping("/update")
    public Map<String, Object> updateUser(@NonNull HttpServletRequest request, @RequestBody Map<String, Object> userMap) {
        String token = request.getHeader("Authorization");
        
        token = token.substring(7);

        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        User user = userMapper.selectById(Integer.parseInt(userId));

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserDetailImpl loginUser = new UserDetailImpl(user);
        User loginuser = loginUser.getUser();
        try {
            return userUpdateService.updateUser(
                loginuser,
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
