package com.newer.jay.demo.controller;

import com.newer.jay.demo.dto.AdminAttractionResponseDTO;
import com.newer.jay.demo.service.admin.AdminAttractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/attractions")
@CrossOrigin(origins = "*")
public class AdminAttractionController {

    @Autowired
    private AdminAttractionService adminAttractionService;

    /**
     * 获取景点列表（分页）
     */
    @GetMapping
    public ResponseEntity<?> getAttractions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            List<AdminAttractionResponseDTO> attractions = adminAttractionService.getAttractions(page, pageSize, keyword);
            long total = adminAttractionService.getAttractionsCount(keyword);
            
            return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取景点列表成功",
                "data", Map.of(
                    "data", attractions,
                    "total", total,
                    "page", page,
                    "pageSize", pageSize
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "message", "获取景点列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据ID获取景点详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAttractionById(@PathVariable Long id) {
        try {
            AdminAttractionResponseDTO attraction = adminAttractionService.getAttractionById(id);
            if (attraction != null) {
                return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "获取景点详情成功",
                    "data", attraction
                ));
            } else {
                return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "message", "景点不存在"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "message", "获取景点详情失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 创建新景点
     */
    @PostMapping
    public ResponseEntity<?> createAttraction(@RequestBody Map<String, Object> attractionData) {
        try {
            AdminAttractionResponseDTO newAttraction = adminAttractionService.createAttraction(attractionData);
            return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "创建景点成功",
                "data", newAttraction
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "message", "创建景点失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 更新景点信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAttraction(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> attractionData) {
        try {
            AdminAttractionResponseDTO updatedAttraction = adminAttractionService.updateAttraction(id, attractionData);
            if (updatedAttraction != null) {
                return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "更新景点成功",
                    "data", updatedAttraction
                ));
            } else {
                return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "message", "景点不存在"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "message", "更新景点失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 删除景点
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttraction(@PathVariable Long id) {
        try {
            boolean deleted = adminAttractionService.deleteAttraction(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "删除景点成功"
                ));
            } else {
                return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "message", "景点不存在"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "message", "删除景点失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 批量删除景点
     */
    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteAttractions(@RequestBody Map<String, List<Long>> requestBody) {
        try {
            List<Long> ids = requestBody.get("ids");
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "请提供要删除的景点ID列表"
                ));
            }
            
            int deletedCount = adminAttractionService.batchDeleteAttractions(ids);
            return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "批量删除景点成功",
                "data", Map.of("deletedCount", deletedCount)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "message", "批量删除景点失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取景点统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            Map<String, Object> statistics = adminAttractionService.getStatistics();
            return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "获取统计信息成功",
                "data", statistics
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "message", "获取统计信息失败: " + e.getMessage()
            ));
        }
    }
}
