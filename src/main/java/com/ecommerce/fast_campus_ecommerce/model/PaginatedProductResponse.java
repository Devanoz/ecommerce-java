package com.ecommerce.fast_campus_ecommerce.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaginatedProductResponse {
    private List<ProductResponse> data;
    private int pageNo;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static PaginatedProductResponse from(List<ProductResponse> productResponses, int pageNo, long totalElements, int totalPages, boolean last) {
        return PaginatedProductResponse.builder()
                .data(productResponses)
                .pageNo(pageNo)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(last)
                .build();
    }
}
