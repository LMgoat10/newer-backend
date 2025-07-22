package com.newer.jay.demo.config;

import com.newer.jay.demo.service.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    
    @Autowired
    private AdminAuthenticationTokenFilter adminAuthenticationTokenFilter;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // configure SecurityFilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 启用 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // 禁用 CSRF 保护
                .csrf(csrf -> csrf.disable())
                // 前后端分离是无状态的，不需要session了，直接禁用。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        // 允许所有OPTIONS请求
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 管理员API需要ADMIN权限
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                        // 景点管理API需要ADMIN权限
                        .requestMatchers("/api/attractions/admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                        // 允许直接访问授权登录接口
                        .requestMatchers(HttpMethod.POST, "/api/auth/*").permitAll()
                        // 允许邮件相关接口（包括GET和POST）
                        .requestMatchers("/mail/**").permitAll()
                        // 允许测试接口（不需要认证）
                        .requestMatchers("/alipay/**").permitAll()
                        // 允许景点相关接口匿名访问（除了admin路径）
                        .requestMatchers("/api/attractions", "/api/attractions/**").permitAll()
                        // 允许 SpringMVC 的默认错误地址匿名访问
                        .requestMatchers("/", "/error", "/api/auth/login", "/api/auth/register", "/api/auth/me", "/api/auth/check-email", "/api/upload/avatar", "/api/upload/image", "/uploads/**").permitAll()
                        // 其他所有接口必须有Authority信息，Authority在登录成功后的UserDetailsImpl对象中默认设置"ROLE_USER"
                        .requestMatchers("/**").hasAnyAuthority("ROLE_USER")
                        // 允许任意请求被已登录用户访问，不检查Authority
                        .anyRequest().authenticated())
                // 先添加管理员过滤器，再添加JWT过滤器
                .addFilterBefore(adminAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationTokenFilter, AdminAuthenticationTokenFilter.class);

        return http.build();
    }
}
