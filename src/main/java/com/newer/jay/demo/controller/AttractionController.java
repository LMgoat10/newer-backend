package com.newer.jay.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.AttractionRequestDTO;
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

    // ===== 管理员专用接口 =====

    /**
     * 获取所有景点列表（管理员专用，包括非活跃状态）
     */
    @GetMapping("/admin/list")
    public ResponseEntity<Map<String, Object>> getAllAttractions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cityName,
            @RequestParam(required = false) String provinceName,
            @RequestParam(required = false) String status) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Page<AttractionResponseDTO> attractions = attractionService.getAllAttractionList(
                page, size, keyword, cityName, provinceName, status);
            
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
     * 创建新景点（管理员）
     */
    @PostMapping("/admin")
    public ResponseEntity<Map<String, Object>> createAttraction(@RequestBody AttractionRequestDTO requestDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            AttractionResponseDTO attraction = attractionService.createAttraction(requestDTO);
            
            response.put("success", true);
            response.put("message", "景点创建成功");
            response.put("data", attraction);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "景点创建失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新景点信息（管理员）
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<Map<String, Object>> updateAttraction(
            @PathVariable Long id,
            @RequestBody AttractionRequestDTO requestDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            AttractionResponseDTO attraction = attractionService.updateAttraction(id, requestDTO);
            
            response.put("success", true);
            response.put("message", "景点更新成功");
            response.put("data", attraction);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "景点更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除景点（管理员）
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Map<String, Object>> deleteAttraction(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = attractionService.deleteAttraction(id);
            
            if (success) {
                response.put("success", true);
                response.put("message", "景点删除成功");
            } else {
                response.put("success", false);
                response.put("message", "景点删除失败，可能景点不存在");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "景点删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量删除景点（管理员）
     */
    @DeleteMapping("/admin/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteAttractions(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = attractionService.batchDeleteAttractions(ids);
            
            if (success) {
                response.put("success", true);
                response.put("message", "批量删除成功");
            } else {
                response.put("success", false);
                response.put("message", "批量删除失败");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新景点状态（管理员）
     */
    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<Map<String, Object>> updateAttractionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = attractionService.updateAttractionStatus(id, status);
            
            if (success) {
                response.put("success", true);
                response.put("message", "状态更新成功");
            } else {
                response.put("success", false);
                response.put("message", "状态更新失败");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "状态更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
