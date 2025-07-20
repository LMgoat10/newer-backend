package com.newer.jay.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.newer.jay.demo.dto.CartItemRequestDTO;
import com.newer.jay.demo.dto.CartItemResponseDTO;
import com.newer.jay.demo.entity.Attraction;
import com.newer.jay.demo.entity.CartItem;
import com.newer.jay.demo.mapper.AttractionMapper;
import com.newer.jay.demo.mapper.CartItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    
    @Autowired
    private CartItemMapper cartItemMapper;
    
    @Autowired
    private AttractionMapper attractionMapper;

    /**
     * 获取用户购物车列表
     */
    public List<CartItemResponseDTO> getCartItems(Integer userId) {
        try {
            LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CartItem::getUserId, userId)
                       .orderByDesc(CartItem::getCreatedAt);
            
            List<CartItem> cartItems = cartItemMapper.selectList(queryWrapper);
            
            return cartItems.stream().map(cartItem -> {
                Attraction attraction = attractionMapper.selectById(cartItem.getAttractionId());
                return CartItemResponseDTO.fromEntities(cartItem, attraction);
            }).collect(Collectors.toList());
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取购物车失败: " + e.getMessage());
        }
    }

    /**
     * 添加商品到购物车
     */
    public String addToCart(Integer userId, CartItemRequestDTO request) {
        try {
            // 检查景点是否存在
            Attraction attraction = attractionMapper.selectById(request.getAttractionId());
            if (attraction == null) {
                return "景点不存在";
            }
            
            // 检查是否已存在相同的购物车项
            LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CartItem::getUserId, userId)
                       .eq(CartItem::getAttractionId, request.getAttractionId())
                       .eq(CartItem::getTicketType, request.getTicketType())
                       .eq(CartItem::getVisitDate, request.getVisitDate());
            
            CartItem existingItem = cartItemMapper.selectOne(queryWrapper);
            
            if (existingItem != null) {
                // 如果已存在，增加数量
                existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
                cartItemMapper.updateById(existingItem);
            } else {
                // 创建新的购物车项
                CartItem cartItem = new CartItem();
                cartItem.setUserId(userId);
                cartItem.setAttractionId(request.getAttractionId());
                cartItem.setTicketType(request.getTicketType());
                cartItem.setTicketPrice(request.getTicketPrice());
                cartItem.setQuantity(request.getQuantity());
                cartItem.setVisitDate(request.getVisitDate());
                
                cartItemMapper.insert(cartItem);
            }
            
            return "添加到购物车成功";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "添加到购物车失败: " + e.getMessage();
        }
    }

    /**
     * 更新购物车商品数量
     */
    public String updateCartItem(Integer userId, Long cartItemId, Integer quantity) {
        try {
            CartItem cartItem = cartItemMapper.selectById(cartItemId);
            
            if (cartItem == null) {
                return "购物车项不存在";
            }
            
            if (!cartItem.getUserId().equals(userId)) {
                return "无权限操作此购物车项";
            }
            
            if (quantity <= 0) {
                // 如果数量为0或负数，删除该项
                cartItemMapper.deleteById(cartItemId);
                return "已从购物车中移除";
            } else {
                cartItem.setQuantity(quantity);
                cartItemMapper.updateById(cartItem);
                return "更新成功";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "更新失败: " + e.getMessage();
        }
    }

    /**
     * 删除购物车商品
     */
    public String removeFromCart(Integer userId, Long cartItemId) {
        try {
            CartItem cartItem = cartItemMapper.selectById(cartItemId);
            
            if (cartItem == null) {
                return "购物车项不存在";
            }
            
            if (!cartItem.getUserId().equals(userId)) {
                return "无权限操作此购物车项";
            }
            
            cartItemMapper.deleteById(cartItemId);
            return "删除成功";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "删除失败: " + e.getMessage();
        }
    }

    /**
     * 清空用户购物车
     */
    public String clearCart(Integer userId) {
        try {
            LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CartItem::getUserId, userId);
            
            cartItemMapper.delete(queryWrapper);
            return "清空购物车成功";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "清空购物车失败: " + e.getMessage();
        }
    }

    /**
     * 获取购物车总计信息
     */
    public CartSummary getCartSummary(Integer userId) {
        try {
            LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CartItem::getUserId, userId);
            
            List<CartItem> cartItems = cartItemMapper.selectList(queryWrapper);
            
            int totalItems = 0;
            BigDecimal totalPrice = BigDecimal.ZERO;
            
            for (CartItem item : cartItems) {
                totalItems += item.getQuantity();
                totalPrice = totalPrice.add(item.getTicketPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            
            return new CartSummary(totalItems, totalPrice, BigDecimal.ZERO);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new CartSummary(0, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    // 内部类用于购物车汇总信息
    public static class CartSummary {
        public final Integer totalItems;
        public final BigDecimal totalPrice;
        public final BigDecimal totalSavings;
        
        public CartSummary(Integer totalItems, BigDecimal totalPrice, BigDecimal totalSavings) {
            this.totalItems = totalItems;
            this.totalPrice = totalPrice;
            this.totalSavings = totalSavings;
        }
    }
}
