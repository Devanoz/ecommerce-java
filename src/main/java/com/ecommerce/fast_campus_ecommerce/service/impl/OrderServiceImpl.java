package com.ecommerce.fast_campus_ecommerce.service.impl;

import com.ecommerce.fast_campus_ecommerce.common.errors.ResourceNotFoundException;
import com.ecommerce.fast_campus_ecommerce.entity.*;
import com.ecommerce.fast_campus_ecommerce.model.CheckoutRequest;
import com.ecommerce.fast_campus_ecommerce.model.OrderItemResponse;
import com.ecommerce.fast_campus_ecommerce.model.OrderStatus;
import com.ecommerce.fast_campus_ecommerce.repository.*;
import com.ecommerce.fast_campus_ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final UserAddressRepository userAddressRepository;
    private final ProductRepository productRepository;

    @Override
    public Order checkout(CheckoutRequest checkoutRequest) {
        List<CartItem> selectedItems = cartItemRepository.findAllById(checkoutRequest.getSelectedCartItemIds());
        if(selectedItems.isEmpty()) {
            throw new ResourceNotFoundException("No carts item found for checkout");
        }

        UserAddress shippingAdress = userAddressRepository.findById(checkoutRequest.getUserAddressId())
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Shipping address with id " + checkoutRequest.getUserAddressId() + "is not found"
                ));

        Order order = Order.builder()
                .userId(checkoutRequest.getUserId())
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .taxFee(BigDecimal.ZERO)
                .subtotal(BigDecimal.ZERO)
                .shippingFee(BigDecimal.ZERO)
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = selectedItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .orderId(savedOrder.getOrderId())
                        .productId(cartItem.getProductId())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .userAddressId(shippingAdress.getUserAddressId())
                        .build()).toList();

        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(selectedItems);

        BigDecimal totalAmout = orderItems.stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        savedOrder.setTotalAmount(totalAmout);
        return orderRepository.save(savedOrder);
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> findOrderByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public void cancelOrder(Long cancelOrder)  {
        Order order = orderRepository.findById(cancelOrder)
                .orElseThrow(()-> new ResourceNotFoundException("Order with id %s".formatted(cancelOrder)));
        if(!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new IllegalStateException("Only pending orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public List<OrderItemResponse> findOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        if(orderItems.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .toList();
        List<Long> shippingAddressIds = orderItems.stream()
                .map(OrderItem::getUserAddressId)
                .toList();

        List<Product> products = productRepository.findAllById(productIds);
        List<UserAddress> shippingAddress = userAddressRepository.findAllById(shippingAddressIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));
        Map<Long, UserAddress> userAddressMap = shippingAddress.stream()
                .collect(Collectors.toMap(UserAddress::getUserAddressId, Function.identity()));
        return orderItems.stream()
                .map(orderItem -> {
                    Product product = productMap.get(orderItem.getProductId());
                    UserAddress userAddress = userAddressMap.get(orderItem.getUserAddressId());
                    if(product == null) {
                        throw new ResourceNotFoundException("Product with id" + orderItem.getProductId() + "not found");
                    }
                    if(userAddress == null) {
                        throw new ResourceNotFoundException("User address with id" + orderItem.getUserAddressId() + "not found");
                    }
                    return OrderItemResponse.fromOrderItemProductAndAdress(orderItem, product,userAddress);
                }).toList();
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("Order with id %s".formatted(orderId)));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Override
    public Double calculateOrderTotal(Long orderId) {
        return orderItemRepository.calculateTotalOrder(orderId);
    }
}
