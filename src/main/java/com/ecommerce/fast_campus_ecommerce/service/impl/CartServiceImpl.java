package com.ecommerce.fast_campus_ecommerce.service.impl;

import com.ecommerce.fast_campus_ecommerce.common.errors.BadRequestException;
import com.ecommerce.fast_campus_ecommerce.common.errors.ResourceNotFoundException;
import com.ecommerce.fast_campus_ecommerce.entity.Cart;
import com.ecommerce.fast_campus_ecommerce.entity.CartItem;
import com.ecommerce.fast_campus_ecommerce.entity.Product;
import com.ecommerce.fast_campus_ecommerce.model.CartItemResponse;
import com.ecommerce.fast_campus_ecommerce.repository.CartItemRepository;
import com.ecommerce.fast_campus_ecommerce.repository.CartRepository;
import com.ecommerce.fast_campus_ecommerce.repository.ProductRepository;
import com.ecommerce.fast_campus_ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public void addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));
        if(product.getUserId().equals(userId)) {
            throw new BadRequestException("You can't add your own product to your cart");
        }

        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId);
        if(existingCartItemOpt.isPresent()) {
            CartItem existingItem = existingCartItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cartId(cart.getCartId())
                    .productId(product.getProductId())
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    public void updateCartItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found user id: " + userId));
        Optional<CartItem> cartItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId);
        if(cartItemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cart item not found for product id: " + productId);
        }

        CartItem cartItem = cartItemOpt.get();
        if(quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    public void removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found user id: " + userId));
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if(cartItemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cart item not found for id: " + cartItemId);
        }

        CartItem cartItem = cartItemOpt.get();
        if(!cartItem.getCartId().equals(cart.getCartId())) {
            throw new BadRequestException("Cart item does not belong to the user's cart");
        }
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found user id: " + userId));
        cartItemRepository.deleteAllByCartId(cart.getCartId());
    }

    @Override
    public List<CartItemResponse> getCartItems(Long userId) {
        List<CartItem> cartItems = cartItemRepository.getUserCartItems(userId);
        if(cartItems.isEmpty()) {
            return List.of();
        }
        List<Long> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .toList();

        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        return cartItems.stream()
                .map(cartItem -> {
                    Product product = productMap.get(cartItem.getProductId());
                   if(product == null) {
                       log.warn("Product not found for cart item id: {}", cartItem.getCartItemId());
                       throw new ResourceNotFoundException("Product not found for cart item id: " + cartItem.getCartItemId());
                   }
                   return CartItemResponse.fromCartItemAndProduct(cartItem, product);
                })
                .toList();
    }
}
