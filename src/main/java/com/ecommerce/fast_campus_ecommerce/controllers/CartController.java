package com.ecommerce.fast_campus_ecommerce.controllers;

import com.ecommerce.fast_campus_ecommerce.model.AddToCartRequest;
import com.ecommerce.fast_campus_ecommerce.model.CartItemResponse;
import com.ecommerce.fast_campus_ecommerce.model.UserInfo;
import com.ecommerce.fast_campus_ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    @PostMapping("/items")
    public ResponseEntity<Void> addItemToCart(@Valid @RequestBody AddToCartRequest addToCartRequest, Authentication authentication) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        cartService.addItemToCart(userInfo.getUser().getUserId(), addToCartRequest.getProductd(), addToCartRequest.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/items")
    public ResponseEntity<Void> updateCartItemQuantity(@Valid @RequestBody AddToCartRequest addToCartRequest, Authentication authentication) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        cartService.updateCartItemQuantity(userInfo.getUser().getUserId(), addToCartRequest.getProductd(), addToCartRequest.getQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable("id") Long cartItemId,Authentication authentication) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        cartService.removeItemFromCart(userInfo.getUser().getUserId(), cartItemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemResponse>> getCartItems(Authentication authentication) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        List<CartItemResponse> cartItems = cartService.getCartItems(userInfo.getUser().getUserId());
        return ResponseEntity.ok(cartItems);
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        cartService.clearCart(userInfo.getUser().getUserId());
        return ResponseEntity.ok().build();
    }

}
