package com.newer.jay.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员更新用户DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateDTO {
    private String name;
    private String email;
    private String phone;
    private String status;
}
