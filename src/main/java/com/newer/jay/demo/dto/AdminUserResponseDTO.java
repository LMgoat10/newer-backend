package com.newer.jay.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 管理员用户响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponseDTO {
    private Integer status;
    private String message;
    private AdminUserPageData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminUserPageData {
        private Long total;
        private List<AdminUserDTO> users;
    }

    @Data
    @NoArgsConstructor  
    @AllArgsConstructor
    public static class AdminUserDTO {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private Double balance;
        private String status;
        private String joinDate;
        private Integer totalOrders;
        private String role;
    }
}
