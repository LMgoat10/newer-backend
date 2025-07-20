package com.newer.jay.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newer.jay.demo.dto.AttractionRequestDTO;
import com.newer.jay.demo.dto.AttractionResponseDTO;
import com.newer.jay.demo.entity.Attraction;
import com.newer.jay.demo.mapper.AttractionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttractionService {
    
    @Autowired
    private AttractionMapper attractionMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // ===== 管理员专用方法 =====

    /**
     * 创建新景点（管理员）
     */
    public AttractionResponseDTO createAttraction(AttractionRequestDTO requestDTO) {
        try {
            Attraction attraction = convertToEntity(requestDTO);
            
            int result = attractionMapper.insert(attraction);
            if (result > 0) {
                return AttractionResponseDTO.fromEntity(attraction);
            } else {
                throw new RuntimeException("景点创建失败");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("景点创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新景点信息（管理员）
     */
    public AttractionResponseDTO updateAttraction(Long id, AttractionRequestDTO requestDTO) {
        try {
            Attraction existing = attractionMapper.selectById(id);
            if (existing == null) {
                throw new RuntimeException("景点不存在");
            }
            
            Attraction attraction = convertToEntity(requestDTO);
            attraction.setId(id);
            attraction.setCreatedAt(existing.getCreatedAt()); // 保持创建时间不变
            
            int result = attractionMapper.updateById(attraction);
            if (result > 0) {
                return AttractionResponseDTO.fromEntity(attractionMapper.selectById(id));
            } else {
                throw new RuntimeException("景点更新失败");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("景点更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除景点（管理员）
     */
    public boolean deleteAttraction(Long id) {
        try {
            int result = attractionMapper.deleteById(id);
            return result > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("景点删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除景点（管理员）
     */
    public boolean batchDeleteAttractions(List<Long> ids) {
        try {
            int result = attractionMapper.deleteBatchIds(ids);
            return result > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 更新景点状态（管理员）
     */
    public boolean updateAttractionStatus(Long id, String status) {
        try {
            Attraction attraction = attractionMapper.selectById(id);
            if (attraction == null) {
                return false;
            }
            
            attraction.setStatus(Attraction.AttractionStatus.valueOf(status));
            int result = attractionMapper.updateById(attraction);
            return result > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("状态更新失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有景点列表（管理员专用，包括非活跃状态）
     */
    public Page<AttractionResponseDTO> getAllAttractionList(int page, int size, String keyword, String cityName, String provinceName, String status) {
        try {
            Page<Attraction> attractionPage = new Page<>(page, size);
            LambdaQueryWrapper<Attraction> queryWrapper = new LambdaQueryWrapper<>();
            
            // 状态筛选
            if (StringUtils.hasText(status)) {
                queryWrapper.eq(Attraction::getStatus, Attraction.AttractionStatus.valueOf(status));
            }
            
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
            
            // 按创建时间降序排列
            queryWrapper.orderByDesc(Attraction::getCreatedAt);
            
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
     * 将DTO转换为实体类
     */
    private Attraction convertToEntity(AttractionRequestDTO requestDTO) {
        try {
            Attraction attraction = new Attraction();
            attraction.setName(requestDTO.getName());
            attraction.setDescription(requestDTO.getDescription());
            attraction.setLocation(requestDTO.getLocation());
            attraction.setAddress(requestDTO.getAddress());
            attraction.setCityName(requestDTO.getCityName());
            attraction.setProvinceName(requestDTO.getProvinceName());
            attraction.setAreaName(requestDTO.getAreaName());
            attraction.setPrice(requestDTO.getPrice());
            attraction.setTotalTickets(requestDTO.getTotalTickets());
            attraction.setAvailableTickets(requestDTO.getAvailableTickets());
            
            // 时间字符串转换为LocalTime
            if (StringUtils.hasText(requestDTO.getOpenTime())) {
                attraction.setOpenTime(LocalTime.parse(requestDTO.getOpenTime(), DateTimeFormatter.ofPattern("HH:mm")));
            }
            if (StringUtils.hasText(requestDTO.getCloseTime())) {
                attraction.setCloseTime(LocalTime.parse(requestDTO.getCloseTime(), DateTimeFormatter.ofPattern("HH:mm")));
            }
            
            attraction.setOpenTimeStr(requestDTO.getOpenTimeStr());
            attraction.setRating(requestDTO.getRating());
            attraction.setReviewCount(requestDTO.getReviewCount());
            attraction.setPhone(requestDTO.getPhone());
            attraction.setWebsite(requestDTO.getWebsite());
            
            // 列表转JSON字符串
            if (requestDTO.getPicList() != null && !requestDTO.getPicList().isEmpty()) {
                attraction.setPicList(objectMapper.writeValueAsString(requestDTO.getPicList()));
            }
            if (requestDTO.getTags() != null && !requestDTO.getTags().isEmpty()) {
                attraction.setTags(objectMapper.writeValueAsString(requestDTO.getTags()));
            }
            
            // 状态设置
            if (StringUtils.hasText(requestDTO.getStatus())) {
                attraction.setStatus(Attraction.AttractionStatus.valueOf(requestDTO.getStatus()));
            } else {
                attraction.setStatus(Attraction.AttractionStatus.ACTIVE);
            }
            
            return attraction;
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("数据格式转换失败: " + e.getMessage());
        }
    }
}
