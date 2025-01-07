package com.ecommerce.fast_campus_ecommerce.service.impl;


import com.ecommerce.fast_campus_ecommerce.common.errors.ResourceNotFoundException;
import com.ecommerce.fast_campus_ecommerce.entity.Category;
import com.ecommerce.fast_campus_ecommerce.entity.Product;
import com.ecommerce.fast_campus_ecommerce.entity.ProductCategory;
import com.ecommerce.fast_campus_ecommerce.model.CategoryResponse;
import com.ecommerce.fast_campus_ecommerce.model.PaginatedProductResponse;
import com.ecommerce.fast_campus_ecommerce.model.ProductRequest;
import com.ecommerce.fast_campus_ecommerce.model.ProductResponse;
import com.ecommerce.fast_campus_ecommerce.repository.CategoryRepository;
import com.ecommerce.fast_campus_ecommerce.repository.ProductCategoryRepository;
import com.ecommerce.fast_campus_ecommerce.repository.ProductRepository;
import com.ecommerce.fast_campus_ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(product -> {
            List<CategoryResponse> productCategories = getProductCategories(product.getProductId());
            return ProductResponse.fromProductAndCategories(product, productCategories);
        }).toList();
    }

    @Override
    public PaginatedProductResponse findAllByPage(int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPages = productRepository.findAllByPage(pageable);
        return getPaginatedProductResponse(productPages);
    }

    private PaginatedProductResponse getPaginatedProductResponse(Page<Product> productPages) {
        List<ProductResponse> productResponses = productPages.stream().map(product -> {
            List<CategoryResponse> productCategories = getProductCategories(product.getProductId());
            return ProductResponse.fromProductAndCategories(product, productCategories);
        }).toList();
        return PaginatedProductResponse.from(productResponses,
                productPages.getNumber(),
                productPages.getTotalElements(),
                productPages.getTotalPages(),
                productPages.isLast());
    }

    @Override
    public PaginatedProductResponse findAllByNameAndPageable(String name, int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productL2ist = productRepository.findByNamePageable(name, pageable);
        return getPaginatedProductResponse(productL2ist);
    }

    @Override
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        List<CategoryResponse> productCategories = getProductCategories(product.getProductId());
        return ProductResponse.fromProductAndCategories(product, productCategories);
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest productRequest) {
        List<Category> categories = getCategoriesByIds(productRequest.getCategoryIds());

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .userId(productRequest.getUser().getUserId())
                .price(productRequest.getPrice())
                .stockQuantity(productRequest.getStockQuantity())
                .weight(productRequest.getWeight())
                .build();

        Product savedProduct = productRepository.save(product);
        List<ProductCategory> productCategories = categories.stream().map(category -> {
            ProductCategory productCategory = new ProductCategory();
            ProductCategory.ProductCategoryId productCategoryId = new ProductCategory.ProductCategoryId();
            productCategoryId.setCategoryId(category.getCategoryId());
            productCategoryId.setProductId(savedProduct.getProductId());
            productCategory.setId(productCategoryId);
            return productCategory;
        }).toList();
        productCategoryRepository.saveAll(productCategories);

        List<CategoryResponse> categoryResponses = categories.stream().map(CategoryResponse::fromCategory).toList();

        return ProductResponse.fromProductAndCategories(savedProduct, categoryResponses);
    }

    @Override
    @Transactional
    public ProductResponse update(Long productId, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        List<Category> categories = getCategoriesByIds(productRequest.getCategoryIds());

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setStockQuantity(productRequest.getStockQuantity());
        existingProduct.setWeight(productRequest.getWeight());
        productRepository.save(existingProduct);

        List<ProductCategory> existingProductCategories = productCategoryRepository.findCategoriesByProductId(productId);
        productCategoryRepository.deleteAll(existingProductCategories);

        List<ProductCategory> productCategories = categories.stream().map(category -> {
            ProductCategory prouctCategory = ProductCategory.builder().build();
            ProductCategory.ProductCategoryId productCategoryId = new ProductCategory.ProductCategoryId();
            productCategoryId.setCategoryId(category.getCategoryId());
            productCategoryId.setProductId(productId);
            prouctCategory.setId(productCategoryId);
            return prouctCategory;
        }).toList();

        productCategoryRepository.saveAll(productCategories);

        List<CategoryResponse> categoryResponses = categories.stream().map(CategoryResponse::fromCategory).toList();

        return ProductResponse.fromProductAndCategories(existingProduct, categoryResponses);
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        List<ProductCategory> categoriesByProductId = productCategoryRepository.findCategoriesByProductId(id);
        productCategoryRepository.deleteAll(categoriesByProductId);
        productRepository.delete(product);
    }

    private List<Category> getCategoriesByIds(List<Long> categoryIds) {
        return categoryIds.stream().map(categoryId -> categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found for id : " + categoryId))).toList();
    }

    private List<CategoryResponse> getProductCategories(Long productId) {
        List<ProductCategory> productCategories = productCategoryRepository.findCategoriesByProductId(productId);
        List<Long> categoryIds = productCategories.stream().map(productCategory -> productCategory.getId().getCategoryId()).toList();
        return categoryRepository.findAllById(categoryIds).stream().map(CategoryResponse::fromCategory).toList();
    }
}


