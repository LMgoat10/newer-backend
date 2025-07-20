package com.newer.jay.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newer.jay.demo.entity.Attraction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttractionMapper extends BaseMapper<Attraction> {
    // MyBatis-Plus提供的基础CRUD方法已经足够使用
}
