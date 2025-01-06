package com.ecommerce.fast_campus_ecommerce.service;

import com.ecommerce.fast_campus_ecommerce.model.PaginatedProductResponse;
import com.ecommerce.fast_campus_ecommerce.model.ProductRequest;
import com.ecommerce.fast_campus_ecommerce.model.ProductResponse;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProductService {
    List<ProductResponse> findAll();

    PaginatedProductResponse findAllByPage(int page, int size, Sort sort);

    ProductResponse findById(Long id);

    ProductResponse create(ProductRequest productRequest);

    ProductResponse update(Long productId, ProductRequest productRequest);

    void delete(Long id);

    PaginatedProductResponse findAllByNameAndPageable(String name, int page, int size, Sort sort);
}
