package com.ecommerce.fast_campus_ecommerce.model;

import com.ecommerce.fast_campus_ecommerce.entity.OrderItem;
import com.ecommerce.fast_campus_ecommerce.entity.Product;
import com.ecommerce.fast_campus_ecommerce.entity.UserAddress;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderItemResponse implements Serializable {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
    private UserAddressResponse shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderItemResponse fromOrderItemProductAndAdress(
            OrderItem orderItem,
            Product product,
            UserAddress userAddress) {
        BigDecimal totalPrice = orderItem.getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        return OrderItemResponse.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(product.getProductId())
                .productName(product.getName())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .totalPrice(totalPrice)
                .shippingAddress(UserAddressResponse.fromUserAdress(userAddress))
                .createdAt(orderItem.getCreatedAt())
                .updatedAt(orderItem.getUpdatedAt())
                .build();
    }
}
