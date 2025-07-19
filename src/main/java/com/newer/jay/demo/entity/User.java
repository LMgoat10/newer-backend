package com.newer.jay.demo.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user")
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    
    private String name;
    private String email;
    private String phone;
    private String hashedPassword;
    private String avatarFileName;
    private String memberLevel;
    private Date joinDate;
    private Integer totalOrders;
    private Integer points;
    private Double balance;
    
    @Data
    public static class Preferences{
        private String language;
        private String currency;
        private Map<String, Boolean> notificationSettings;
    }
}
