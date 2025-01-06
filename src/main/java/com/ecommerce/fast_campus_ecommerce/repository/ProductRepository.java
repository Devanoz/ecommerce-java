package com.ecommerce.fast_campus_ecommerce.repository;

import com.ecommerce.fast_campus_ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // SELECT * FROM products WHERE name like ?;
    @Query(value = """
            SELECT * FROM product
            WHERE lower("name") like :name
            """, nativeQuery = true)
    Page<Product> findByNamePageable(String name, Pageable pageable);

    @Query(value = """
            SELECT DISTINCT p.* FROM product p
            JOIN product_category pc ON p.product_id = pc.product_id
            JOIN category c ON pc.category_id = c.category_id
            WHERE lower(c.name) like :categoryName
            """, nativeQuery = true)
    List<Product> findByCategory(String categoryName);

    @Query(value = """
            SELECT * FROM product
            """, nativeQuery = true)
    Page<Product> findAllByPage(Pageable pageable);

    @Query(value = """
            SELECT * FROM product
            WHERE lower(product.name) like lower(concat('%', :name, '%'))
            """, nativeQuery = true)
    Page<Product> findAllByNameAndPageable(String name, Pageable pageable);
}
