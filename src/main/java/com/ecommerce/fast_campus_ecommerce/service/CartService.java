package com.ecommerce.fast_campus_ecommerce.service;

import com.ecommerce.fast_campus_ecommerce.model.CartItemResponse;

import java.util.List;

public interface CartService {
    void addItemToCart(Long userId, Long productId, int quantity);
    void updateCartItemQuantity(Long userId, Long productId, int quantity);
    void removeItemFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
    List<CartItemResponse> getCartItems(Long userId);
}
