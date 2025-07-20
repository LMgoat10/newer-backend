package com.newer.jay.demo.config;

import com.newer.jay.demo.mapper.UserMapper;
import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.service.user.UserDetailImpl;
import com.newer.jay.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        System.out.println("JwtAuthenticationTokenFilter: Processing request: " + request.getMethod() + " " + requestURI);
        
        // 管理员API由专门的过滤器处理，这里跳过
        if (requestURI.startsWith("/api/admin/") || requestURI.startsWith("/api/attractions/admin")) {
            System.out.println("JwtAuthenticationTokenFilter: Skipping admin API request: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            System.err.println("JWT parsing failed: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        User user = userMapper.selectById(Integer.parseInt(userId));

        if (user == null) {
            System.err.println("User not found for ID: " + userId);
            filterChain.doFilter(request, response);
            return;
        }

        UserDetailImpl loginUser = new UserDetailImpl(user);

        // 设置用户权限为ROLE_USER
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginUser, 
            null, 
            loginUser.getAuthorities()  // 从UserDetailImpl获取权限
        );


        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
