package com.newer.jay.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.AttractionResponseDTO;
import com.newer.jay.demo.service.AttractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attractions")
@CrossOrigin(origins = "*")
public class AttractionController {
    
    @Autowired
    private AttractionService attractionService;

    /**
     * 获取景点列表（分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAttractions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cityName,
            @RequestParam(required = false) String provinceName) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Page<AttractionResponseDTO> attractions = attractionService.getAttractionList(
                page, size, keyword, cityName, provinceName);
            
            response.put("success", true);
            response.put("message", "获取景点列表成功");
            response.put("data", Map.of(
                "attractions", attractions.getRecords(),
                "total", attractions.getTotal(),
                "pages", attractions.getPages(),
                "pageNum", attractions.getCurrent(),
                "pageSize", attractions.getSize()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取景点列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据ID获取景点详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAttractionById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            AttractionResponseDTO attraction = attractionService.getAttractionById(id);
            
            if (attraction == null) {
                response.put("success", false);
                response.put("message", "景点不存在");
                return ResponseEntity.notFound().build();
            }
            
            response.put("success", true);
            response.put("message", "获取景点详情成功");
            response.put("data", attraction);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取景点详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取城市列表（用于筛选）
     */
    @GetMapping("/cities")
    public ResponseEntity<Map<String, Object>> getCities() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> cities = attractionService.getCityList();
            
            response.put("success", true);
            response.put("data", cities);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取城市列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取省份列表（用于筛选）
     */
    @GetMapping("/provinces")
    public ResponseEntity<Map<String, Object>> getProvinces() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> provinces = attractionService.getProvinceList();
            
            response.put("success", true);
            response.put("data", provinces);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取省份列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
