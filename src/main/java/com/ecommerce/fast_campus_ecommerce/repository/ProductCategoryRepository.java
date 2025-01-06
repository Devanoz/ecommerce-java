package com.ecommerce.fast_campus_ecommerce.repository;

import com.ecommerce.fast_campus_ecommerce.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategory.ProductCategoryId> {
    @Query(value = """
      SELECT * FROM product_category
      WHERE product_id = :productId
      """, nativeQuery = true)
    List<ProductCategory> findCategoriesByProductId(Long productId);
}
