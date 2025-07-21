package com.newer.jay.demo.controller;

import com.newer.jay.demo.dto.*;
import com.newer.jay.demo.entity.TicketOrder;
import com.newer.jay.demo.service.OrderService;
import com.newer.jay.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @RequestBody OrderCreateRequestDTO requestDTO,
            HttpServletRequest request) {
        try {
            // 从JWT token获取用户ID
            Integer userId = getUserIdFromRequest(request);
            requestDTO.setUserId(userId);

            OrderResponseDTO order = orderService.createOrder(requestDTO);
            ResponseEntity.status(200);
            return ResponseEntity.ok(ApiResponse.success("订单创建成功", order));
        } catch (Exception e) {
            log.error("创建订单失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取用户订单列表（分页）
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponseDTO>>> getUserOrders(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            PageResponse<OrderResponseDTO> orders = orderService.getUserOrders(userId, page, size, status);
            return ResponseEntity.ok(ApiResponse.success("获取订单列表成功", orders));
        } catch (Exception e) {
            log.error("获取订单列表失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderDetails(
            @PathVariable String orderNumber,
            HttpServletRequest request) {
        try {
            // 可以添加用户权限检查
            OrderResponseDTO order = orderService.getOrderDetails(orderNumber);
            return ResponseEntity.ok(ApiResponse.success("获取订单详情成功", order));
        } catch (Exception e) {
            log.error("获取订单详情失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderNumber}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @PathVariable String orderNumber,
            HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromRequest(request);
            orderService.cancelOrder(orderNumber, userId);
            return ResponseEntity.ok(ApiResponse.success("订单取消成功", null));
        } catch (Exception e) {
            log.error("取消订单失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 申请退款
     */
    @PostMapping("/{orderNumber}/refund")
    public ResponseEntity<ApiResponse<String>> requestRefund(
            @PathVariable String orderNumber,
            @RequestBody RefundRequestDTO requestDTO,
            HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromRequest(request);
            requestDTO.setUserId(userId);
            orderService.requestRefund(orderNumber, requestDTO);
            return ResponseEntity.ok(ApiResponse.success("退款申请提交成功", null));
        } catch (Exception e) {
            log.error("申请退款失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 模拟支付订单
     */
    @PostMapping("/{orderNumber}/pay")
    public ResponseEntity<ApiResponse<String>> payOrder(
            @PathVariable String orderNumber,
            @RequestParam String payMethod) {
        try {
            TicketOrder.PayMethod method = TicketOrder.PayMethod.valueOf(payMethod.toUpperCase());
            orderService.processPayment(orderNumber, method);
            return ResponseEntity.ok(ApiResponse.success("支付成功", null));
        } catch (Exception e) {
            log.error("支付失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取用户订单统计
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUserOrderStats(HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromRequest(request);
            List<Map<String, Object>> stats = orderService.getUserOrderStats(userId);
            return ResponseEntity.ok(ApiResponse.success("获取订单统计成功", stats));
        } catch (Exception e) {
            log.error("获取订单统计失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 从请求中获取用户ID
     */
    private Integer getUserIdFromRequest(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Claims claims = JwtUtil.parseJWT(token);
                return Integer.valueOf(claims.getSubject());
            }
            throw new RuntimeException("无效的认证令牌");
        } catch (Exception e) {
            throw new RuntimeException("令牌解析失败: " + e.getMessage());
        }
    }
}
