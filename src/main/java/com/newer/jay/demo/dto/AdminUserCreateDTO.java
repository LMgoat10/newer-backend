package com.newer.jay.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员创建用户DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserCreateDTO {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;
}
