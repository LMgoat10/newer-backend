package com.newer.jay.demo.controller;

import com.newer.jay.demo.dto.CartItemRequestDTO;
import com.newer.jay.demo.dto.CartItemResponseDTO;
import com.newer.jay.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    
    @Autowired
    private CartService cartService;

    /**
     * 获取用户购物车列表
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getCartItems(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CartItemResponseDTO> cartItems = cartService.getCartItems(userId);
            CartService.CartSummary summary = cartService.getCartSummary(userId);
            
            response.put("success", true);
            response.put("message", "获取购物车成功");
            response.put("data", Map.of(
                "items", cartItems,
                "summary", Map.of(
                    "totalItems", summary.totalItems,
                    "totalPrice", summary.totalPrice,
                    "totalSavings", summary.totalSavings
                )
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取购物车失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/{userId}/add")
    public ResponseEntity<Map<String, Object>> addToCart(
            @PathVariable Integer userId,
            @RequestBody CartItemRequestDTO request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = cartService.addToCart(userId, request);
            
            response.put("success", true);
            response.put("message", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "添加到购物车失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/{userId}/update/{cartItemId}")
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @PathVariable Integer userId,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = cartService.updateCartItem(userId, cartItemId, quantity);
            
            response.put("success", true);
            response.put("message", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @PathVariable Integer userId,
            @PathVariable Long cartItemId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = cartService.removeFromCart(userId, cartItemId);
            
            response.put("success", true);
            response.put("message", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Map<String, Object>> clearCart(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = cartService.clearCart(userId);
            
            response.put("success", true);
            response.put("message", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "清空购物车失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取购物车商品数量（用于导航栏显示）
     */
    @GetMapping("/{userId}/count")
    public ResponseEntity<Map<String, Object>> getCartItemCount(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            CartService.CartSummary summary = cartService.getCartSummary(userId);
            
            response.put("success", true);
            response.put("count", summary.totalItems);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("count", 0);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
