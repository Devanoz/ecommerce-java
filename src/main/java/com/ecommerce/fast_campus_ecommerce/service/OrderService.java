package com.ecommerce.fast_campus_ecommerce.service;

import com.ecommerce.fast_campus_ecommerce.entity.Order;
import com.ecommerce.fast_campus_ecommerce.model.CheckoutRequest;
import com.ecommerce.fast_campus_ecommerce.model.OrderItemResponse;
import com.ecommerce.fast_campus_ecommerce.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order checkout(CheckoutRequest checkoutRequest);

    Optional<Order> findOrderById(Long orderId);

    List<Order> findOrderByUserId(Long userId);

    List<Order> findOrdersByStatus(OrderStatus status);

    void cancelOrder(Long cancelOrder);

    List<OrderItemResponse> findOrderItemsByOrderId(Long orderId);

    void updateOrderStatus(Long orderId, OrderStatus newStatus);

    Double calculateOrderTotal(Long orderId);
}
