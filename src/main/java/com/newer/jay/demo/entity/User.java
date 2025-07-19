package com.newer.jay.demo.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user", autoResultMap = true)
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId;
    
    private String name;
    private String email;
    private String phone;
    private String hashedPassword;
    private String avatarFileName;
    private String memberLevel;
    private Integer memberPoints;
    private Date joinDate;
    private String idCard;
    private String address;
    private Integer totalOrders;
    private Double balance;
}
