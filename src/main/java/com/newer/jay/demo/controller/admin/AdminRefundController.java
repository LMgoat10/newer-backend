package com.newer.jay.demo.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.AdminRefundResponseDTO;
import com.newer.jay.demo.service.admin.AdminRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/refunds")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminRefundController {

    private final AdminRefundService adminRefundService;

    /**
     * 分页查询退票申请列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRefundRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "ALL") String status) {
        
        System.out.println("AdminRefundController.getRefundRequests called with page=" + page + ", size=" + size + ", keyword=" + keyword + ", status=" + status);
        
        try {
            Page<AdminRefundResponseDTO> result = adminRefundService.getRefundRequests(page, size, keyword, status);
            System.out.println("Service returned: " + result.getTotal() + " total records");
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "获取退票申请列表成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取退票申请列表失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "获取退票申请列表失败: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批准退票申请
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveRefund(@PathVariable String id) {
        System.out.println("AdminRefundController.approveRefund called with id=" + id);
        
        try {
            adminRefundService.approveRefund(id);
            System.out.println("Service returned: success");
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "退票申请批准成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("批准退票申请失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "批准退票申请失败: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 拒绝退票申请
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectRefund(@PathVariable String id, @RequestBody Map<String, String> request) {
        String rejectReason = request.get("reason");
        System.out.println("AdminRefundController.rejectRefund called with id=" + id + ", reason=" + rejectReason);
        
        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", "拒绝理由不能为空");
            
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            adminRefundService.rejectRefund(id, rejectReason);
            System.out.println("Service returned: success");
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "退票申请已拒绝");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("拒绝退票申请失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "拒绝退票申请失败: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取退票统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getRefundStatistics() {
        System.out.println("AdminRefundController.getRefundStatistics called");
        
        try {
            Map<String, Object> statistics = adminRefundService.getRefundStatistics();
            System.out.println("Service returned: " + statistics);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "获取退票统计成功");
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取退票统计失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "获取退票统计失败: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
