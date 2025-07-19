package com.newer.jay.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newer.jay.demo.entity.Wallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface WalletMapper extends BaseMapper<Wallet> {
    
    @Update("UPDATE wallet SET balance = balance + #{amount}, updated_at = NOW() WHERE user_id = #{userId}")
    int increaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
    
    @Update("UPDATE wallet SET balance = balance - #{amount}, updated_at = NOW() WHERE user_id = #{userId} AND balance >= #{amount}")
    int decreaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
