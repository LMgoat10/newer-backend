package com.newer.jay.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.AttractionResponseDTO;
import com.newer.jay.demo.entity.Attraction;
import com.newer.jay.demo.mapper.AttractionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttractionService {
    
    @Autowired
    private AttractionMapper attractionMapper;

    /**
     * 获取景点列表（分页）
     */
    public Page<AttractionResponseDTO> getAttractionList(int page, int size, String keyword, String cityName, String provinceName) {
        try {
            Page<Attraction> attractionPage = new Page<>(page, size);
            LambdaQueryWrapper<Attraction> queryWrapper = new LambdaQueryWrapper<>();
            
            // 只显示活跃的景点
            queryWrapper.eq(Attraction::getStatus, Attraction.AttractionStatus.ACTIVE);
            
            // 搜索条件
            if (StringUtils.hasText(keyword)) {
                queryWrapper.and(wrapper -> wrapper
                    .like(Attraction::getName, keyword)
                    .or()
                    .like(Attraction::getDescription, keyword)
                    .or()
                    .like(Attraction::getAddress, keyword)
                );
            }
            
            if (StringUtils.hasText(cityName)) {
                queryWrapper.eq(Attraction::getCityName, cityName);
            }
            
            if (StringUtils.hasText(provinceName)) {
                queryWrapper.eq(Attraction::getProvinceName, provinceName);
            }
            
            // 按评分降序排列
            queryWrapper.orderByDesc(Attraction::getRating);
            
            Page<Attraction> result = attractionMapper.selectPage(attractionPage, queryWrapper);
            
            // 转换为DTO
            Page<AttractionResponseDTO> dtoPage = new Page<>(page, size, result.getTotal());
            List<AttractionResponseDTO> dtoList = result.getRecords().stream()
                    .map(AttractionResponseDTO::fromEntity)
                    .collect(Collectors.toList());
            dtoPage.setRecords(dtoList);
            
            return dtoPage;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取景点列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取景点详情
     */
    public AttractionResponseDTO getAttractionById(Long id) {
        try {
            Attraction attraction = attractionMapper.selectById(id);
            if (attraction == null) {
                return null;
            }
            return AttractionResponseDTO.fromEntity(attraction);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取景点详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取城市列表（用于筛选）
     */
    public List<String> getCityList() {
        try {
            LambdaQueryWrapper<Attraction> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(Attraction::getCityName)
                       .eq(Attraction::getStatus, Attraction.AttractionStatus.ACTIVE)
                       .groupBy(Attraction::getCityName);
            
            return attractionMapper.selectList(queryWrapper).stream()
                    .map(Attraction::getCityName)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * 获取省份列表（用于筛选）
     */
    public List<String> getProvinceList() {
        try {
            LambdaQueryWrapper<Attraction> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(Attraction::getProvinceName)
                       .eq(Attraction::getStatus, Attraction.AttractionStatus.ACTIVE)
                       .groupBy(Attraction::getProvinceName);
            
            return attractionMapper.selectList(queryWrapper).stream()
                    .map(Attraction::getProvinceName)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
