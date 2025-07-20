package com.newer.jay.demo.config;

import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.service.user.UserDetailImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 管理员认证过滤器
 * 专门处理管理员token认证
 */
@Component
public class AdminAuthenticationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        System.out.println("AdminAuthenticationTokenFilter: Processing request: " + request.getMethod() + " " + requestURI);
        
        // 只对管理员API路径进行拦截
        if (!requestURI.startsWith("/api/admin/") && !requestURI.startsWith("/api/attractions/admin")) {
            System.out.println("AdminAuthenticationTokenFilter: Skipping non-admin request: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        System.out.println("AdminAuthenticationTokenFilter: Processing admin API request: " + requestURI);
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            System.err.println("Admin API request without proper Authorization header: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        // 检查是否是管理员token
        if (token.startsWith("admin-token-")) {
            System.out.println("Processing admin token: " + token);
            
            // 验证管理员token有效性（简单验证时间戳）
            try {
                String timestamp = token.substring("admin-token-".length());
                Long.parseLong(timestamp);
                
                // 创建管理员用户认证对象
                UserDetailImpl userDetail = new UserDetailImpl();
                User adminUser = new User();
                adminUser.setUserId(0L); // 管理员使用特殊ID
                adminUser.setName("admin");
                adminUser.setEmail("admin@admin.com");
                userDetail.setUser(adminUser);
                
                // 设置管理员权限
                List<SimpleGrantedAuthority> authorities = Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ADMIN")
                );
                
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(userDetail, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                
                System.out.println("Admin authentication successful for: " + requestURI);
                filterChain.doFilter(request, response);
                return;
                
            } catch (NumberFormatException e) {
                System.err.println("Invalid admin token format: " + token);
            }
        }

        // 如果不是有效的管理员token，继续过滤链
        System.err.println("Admin API request with invalid token: " + token);
        filterChain.doFilter(request, response);
    }
}
