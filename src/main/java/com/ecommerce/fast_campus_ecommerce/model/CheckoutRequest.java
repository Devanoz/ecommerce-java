package com.ecommerce.fast_campus_ecommerce.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckoutRequest {
    private long userId;

    @NotEmpty(message = "selected_cart_item_ids is required")
    @Size(min = 1, message = "selected_cart_item_ids must have at least one item")
    private List<Long> selectedCartItemIds;

    @NotNull(message = "user_address_id is required")
    private Long userAddressId;
}
