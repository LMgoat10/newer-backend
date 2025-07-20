package com.newer.jay.demo.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.newer.jay.demo.dto.AdminOrderResponseDTO;
import com.newer.jay.demo.service.admin.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminOrderController {
    
    @Autowired
    private AdminOrderService adminOrderService;
    
    /**
     * 分页查询订单列表
     */
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        
        try {
            Page<AdminOrderResponseDTO> orderPage = adminOrderService.getOrders(page, size, keyword, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "查询成功");
            response.put("data", orderPage);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据订单ID获取订单详情
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable String orderId) {
        try {
            AdminOrderResponseDTO order = adminOrderService.getOrderById(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "查询成功");
            response.put("data", order);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 404);
            response.put("message", e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(404).body(response);
        }
    }
    
    /**
     * 删除未付款订单
     */
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable String orderId) {
        try {
            adminOrderService.deleteUnpaidOrder(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "订单删除成功");
            response.put("data", null);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 放票（发货）- 将已付款订单状态改为已放票
     */
    @PostMapping("/orders/{orderId}/release")
    public ResponseEntity<Map<String, Object>> releaseTicket(@PathVariable String orderId) {
        try {
            adminOrderService.releaseTicket(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "放票成功");
            response.put("data", null);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 批量放票
     */
    @PostMapping("/orders/batch-release")
    public ResponseEntity<Map<String, Object>> batchReleaseTickets(@RequestBody List<String> orderIds) {
        try {
            adminOrderService.batchReleaseTickets(orderIds);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "批量放票操作完成");
            response.put("data", null);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取订单统计信息
     */
    @GetMapping("/orders/statistics")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        try {
            AdminOrderService.OrderStatisticsDTO statistics = adminOrderService.getOrderStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "查询成功");
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
