package com.ecommerce.fast_campus_ecommerce.repository;

import com.ecommerce.fast_campus_ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query(value = """
            SELECT oi.* from order_items oi
            JOIN orders o on oi.order_id = o.order_id
            WHERE o.user_id = :userId AND oi.product_id = :productId
            """, nativeQuery = true
    )
    List<OrderItem> findByUserAndProduct(Long userId,Long productId);

    @Query(value = """
            SELECT SUM(oi.price * oi.quantity) from order_items oi
            WHERE oi.order_id = :orderId
            """, nativeQuery = true
    )
    Double calculateTotalOrder(Long orderId);
}
