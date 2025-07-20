package com.newer.jay.demo.service.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newer.jay.demo.dto.AdminAttractionResponseDTO;
import com.newer.jay.demo.dto.AttractionRequestDTO;
import com.newer.jay.demo.entity.Attraction;
import com.newer.jay.demo.mapper.AttractionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminAttractionService {

    @Autowired
    private AttractionMapper attractionMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分页获取景点列表（管理员用）
     */
    public List<AdminAttractionResponseDTO> getAttractions(int page, int pageSize, String keyword) {
        Page<Attraction> attractionPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Attraction> queryWrapper = new LambdaQueryWrapper<>();

        // 搜索条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Attraction::getName, keyword)
                .or()
                .like(Attraction::getDescription, keyword)
                .or()
                .like(Attraction::getAddress, keyword)
                .or()
                .like(Attraction::getCityName, keyword)
                .or()
                .like(Attraction::getProvinceName, keyword)
            );
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc(Attraction::getCreatedAt);

        Page<Attraction> result = attractionMapper.selectPage(attractionPage, queryWrapper);
        
        return result.getRecords().stream()
            .map(this::convertToAdminResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * 获取景点总数
     */
    public long getAttractionsCount(String keyword) {
        LambdaQueryWrapper<Attraction> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Attraction::getName, keyword)
                .or()
                .like(Attraction::getDescription, keyword)
                .or()
                .like(Attraction::getAddress, keyword)
                .or()
                .like(Attraction::getCityName, keyword)
                .or()
                .like(Attraction::getProvinceName, keyword)
            );
        }

        return attractionMapper.selectCount(queryWrapper);
    }

    /**
     * 根据ID获取景点详情
     */
    public AdminAttractionResponseDTO getAttractionById(Long id) {
        Attraction attraction = attractionMapper.selectById(id);
        if (attraction != null) {
            return convertToAdminResponseDTO(attraction);
        }
        return null;
    }

    /**
     * 创建景点
     */
    @Transactional
    public AdminAttractionResponseDTO createAttraction(Map<String, Object> requestData) {
        AttractionRequestDTO requestDTO = convertMapToDTO(requestData);
        Attraction attraction = convertToEntity(requestDTO);
        attractionMapper.insert(attraction);
        return convertToAdminResponseDTO(attraction);
    }

    /**
     * 更新景点
     */
    @Transactional
    public AdminAttractionResponseDTO updateAttraction(Long id, Map<String, Object> requestData) {
        Attraction existingAttraction = attractionMapper.selectById(id);
        if (existingAttraction == null) {
            throw new RuntimeException("景点不存在");
        }

        AttractionRequestDTO requestDTO = convertMapToDTO(requestData);
        Attraction attraction = convertToEntity(requestDTO);
        attraction.setId(id);
        attraction.setCreatedAt(existingAttraction.getCreatedAt());
        
        attractionMapper.updateById(attraction);
        return convertToAdminResponseDTO(attraction);
    }

    /**
     * 删除景点
     */
    @Transactional
    public boolean deleteAttraction(Long id) {
        Attraction attraction = attractionMapper.selectById(id);
        if (attraction == null) {
            return false;
        }
        attractionMapper.deleteById(id);
        return true;
    }

    /**
     * 批量删除景点
     */
    @Transactional
    public int batchDeleteAttractions(List<Long> ids) {
        return attractionMapper.deleteBatchIds(ids);
    }

    /**
     * 更新景点状态
     */
    @Transactional
    public AdminAttractionResponseDTO updateAttractionStatus(Long id, String status) {
        Attraction attraction = attractionMapper.selectById(id);
        if (attraction == null) {
            throw new RuntimeException("景点不存在");
        }

        Attraction.AttractionStatus attractionStatus;
        try {
            attractionStatus = Attraction.AttractionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的状态值: " + status);
        }

        attraction.setStatus(attractionStatus);
        attractionMapper.updateById(attraction);
        
        return convertToAdminResponseDTO(attraction);
    }

    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics() {
        LambdaQueryWrapper<Attraction> queryWrapper = new LambdaQueryWrapper<>();
        
        // 总景点数
        long totalCount = attractionMapper.selectCount(null);
        
        // 活跃景点数
        queryWrapper.eq(Attraction::getStatus, Attraction.AttractionStatus.ACTIVE);
        long activeCount = attractionMapper.selectCount(queryWrapper);
        
        // 非活跃景点数
        queryWrapper.clear();
        queryWrapper.ne(Attraction::getStatus, Attraction.AttractionStatus.ACTIVE);
        long inactiveCount = attractionMapper.selectCount(queryWrapper);
        
        Map<String, Object> statistics = Map.of(
            "totalAttractions", totalCount,
            "activeAttractions", activeCount,
            "inactiveAttractions", inactiveCount,
            "activationRate", totalCount > 0 ? (double) activeCount / totalCount * 100 : 0.0
        );
        
        return statistics;
    }

    /**
     * 将实体转换为管理员响应DTO
     */
    private AdminAttractionResponseDTO convertToAdminResponseDTO(Attraction attraction) {
        AdminAttractionResponseDTO dto = new AdminAttractionResponseDTO();
        
        dto.setId(attraction.getId());
        dto.setName(attraction.getName());
        dto.setDescription(attraction.getDescription());
        dto.setLocation(attraction.getLocation());
        dto.setAddress(attraction.getAddress());
        dto.setCityName(attraction.getCityName());
        dto.setProvinceName(attraction.getProvinceName());
        dto.setAreaName(attraction.getAreaName());
        dto.setPrice(attraction.getPrice());
        dto.setTotalTickets(attraction.getTotalTickets());
        dto.setAvailableTickets(attraction.getAvailableTickets());
        dto.setRating(attraction.getRating());
        dto.setReviewCount(attraction.getReviewCount());
        dto.setPhone(attraction.getPhone());
        dto.setWebsite(attraction.getWebsite());
        dto.setCreatedAt(attraction.getCreatedAt());
        dto.setUpdatedAt(attraction.getUpdatedAt());

        // 处理时间格式
        if (attraction.getOpenTime() != null) {
            dto.setOpenTime(attraction.getOpenTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            dto.setOpenTimeStr(attraction.getOpenTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        if (attraction.getCloseTime() != null) {
            dto.setCloseTime(attraction.getCloseTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        // 处理状态
        if (attraction.getStatus() != null) {
            dto.setStatus(attraction.getStatus().name());
        }

        // 处理图片列表
        if (StringUtils.hasText(attraction.getPicList())) {
            try {
                List<String> picList = objectMapper.readValue(attraction.getPicList(), new TypeReference<List<String>>() {});
                dto.setPicList(picList);
            } catch (JsonProcessingException e) {
                dto.setPicList(new ArrayList<>());
            }
        } else {
            dto.setPicList(new ArrayList<>());
        }

        // 处理标签列表
        if (StringUtils.hasText(attraction.getTags())) {
            try {
                List<String> tags = objectMapper.readValue(attraction.getTags(), new TypeReference<List<String>>() {});
                dto.setTags(tags);
            } catch (JsonProcessingException e) {
                dto.setTags(new ArrayList<>());
            }
        } else {
            dto.setTags(new ArrayList<>());
        }

        // 设置扩展字段
        dto.setTiming(formatTiming(attraction.getOpenTime(), attraction.getCloseTime()));
        dto.setContactInfo(attraction.getPhone());
        dto.setTransportation(attraction.getAddress());

        return dto;
    }

    /**
     * 将请求DTO转换为实体
     */
    private Attraction convertToEntity(AttractionRequestDTO requestDTO) {
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
        attraction.setAvailableTickets(requestDTO.getAvailableTickets() != null ? requestDTO.getAvailableTickets() : requestDTO.getTotalTickets());
        attraction.setPhone(requestDTO.getPhone());
        attraction.setWebsite(requestDTO.getWebsite());

        // 处理时间字符串
        if (StringUtils.hasText(requestDTO.getOpenTime())) {
            attraction.setOpenTime(LocalTime.parse(requestDTO.getOpenTime(), DateTimeFormatter.ofPattern("HH:mm")));
        }
        if (StringUtils.hasText(requestDTO.getCloseTime())) {
            attraction.setCloseTime(LocalTime.parse(requestDTO.getCloseTime(), DateTimeFormatter.ofPattern("HH:mm")));
        }

        // 处理状态
        if (StringUtils.hasText(requestDTO.getStatus())) {
            try {
                attraction.setStatus(Attraction.AttractionStatus.valueOf(requestDTO.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                attraction.setStatus(Attraction.AttractionStatus.ACTIVE); // 默认状态
            }
        } else {
            attraction.setStatus(Attraction.AttractionStatus.ACTIVE);
        }

        // 处理图片列表
        if (requestDTO.getPicList() != null && !requestDTO.getPicList().isEmpty()) {
            try {
                attraction.setPicList(objectMapper.writeValueAsString(requestDTO.getPicList()));
            } catch (JsonProcessingException e) {
                attraction.setPicList("[]");
            }
        } else {
            attraction.setPicList("[]");
        }

        // 处理标签列表
        if (requestDTO.getTags() != null && !requestDTO.getTags().isEmpty()) {
            try {
                attraction.setTags(objectMapper.writeValueAsString(requestDTO.getTags()));
            } catch (JsonProcessingException e) {
                attraction.setTags("[]");
            }
        } else {
            attraction.setTags("[]");
        }

        // 设置默认评分
        attraction.setRating(requestDTO.getRating() != null ? requestDTO.getRating() : BigDecimal.valueOf(4.5));
        attraction.setReviewCount(requestDTO.getReviewCount() != null ? requestDTO.getReviewCount() : 0);

        return attraction;
    }

    /**
     * 格式化营业时间
     */
    private String formatTiming(LocalTime openTime, LocalTime closeTime) {
        if (openTime == null && closeTime == null) {
            return "全天开放";
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String open = openTime != null ? openTime.format(formatter) : "00:00";
        String close = closeTime != null ? closeTime.format(formatter) : "23:59";
        
        return open + " - " + close;
    }

    /**
     * 将Map转换为AttractionRequestDTO
     */
    @SuppressWarnings("unchecked")
    private AttractionRequestDTO convertMapToDTO(Map<String, Object> requestData) {
        AttractionRequestDTO dto = new AttractionRequestDTO();
        
        dto.setName((String) requestData.get("name"));
        dto.setDescription((String) requestData.get("description"));
        dto.setLocation((String) requestData.get("location"));
        dto.setAddress((String) requestData.get("address"));
        dto.setCityName((String) requestData.get("cityName"));
        dto.setProvinceName((String) requestData.get("provinceName"));
        dto.setAreaName((String) requestData.get("areaName"));
        dto.setPhone((String) requestData.get("phone"));
        dto.setWebsite((String) requestData.get("website"));
        dto.setOpenTime((String) requestData.get("openTime"));
        dto.setCloseTime((String) requestData.get("closeTime"));
        dto.setStatus((String) requestData.get("status"));

        // 处理数字字段
        if (requestData.get("price") != null) {
            if (requestData.get("price") instanceof Number) {
                dto.setPrice(BigDecimal.valueOf(((Number) requestData.get("price")).doubleValue()));
            } else if (requestData.get("price") instanceof String) {
                dto.setPrice(new BigDecimal((String) requestData.get("price")));
            }
        }

        if (requestData.get("totalTickets") != null) {
            dto.setTotalTickets(((Number) requestData.get("totalTickets")).intValue());
        }

        if (requestData.get("availableTickets") != null) {
            dto.setAvailableTickets(((Number) requestData.get("availableTickets")).intValue());
        }

        if (requestData.get("rating") != null) {
            if (requestData.get("rating") instanceof Number) {
                dto.setRating(BigDecimal.valueOf(((Number) requestData.get("rating")).doubleValue()));
            } else if (requestData.get("rating") instanceof String) {
                dto.setRating(new BigDecimal((String) requestData.get("rating")));
            }
        }

        if (requestData.get("reviewCount") != null) {
            dto.setReviewCount(((Number) requestData.get("reviewCount")).intValue());
        }

        // 处理列表字段
        if (requestData.get("picList") instanceof List) {
            dto.setPicList((List<String>) requestData.get("picList"));
        }

        if (requestData.get("tags") instanceof List) {
            dto.setTags((List<String>) requestData.get("tags"));
        }

        return dto;
    }
}
