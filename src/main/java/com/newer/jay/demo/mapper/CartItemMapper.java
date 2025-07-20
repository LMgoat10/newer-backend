package com.newer.jay.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newer.jay.demo.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
    // MyBatis-Plus提供的基础CRUD方法已经足够使用
    // 如果需要复杂查询，可以在这里添加自定义方法
}
