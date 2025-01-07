package com.ecommerce.fast_campus_ecommerce.repository;

import com.ecommerce.fast_campus_ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
            SELECT * FROM users u
            WHERE u.username = :username
            """, nativeQuery = true
    )
    Optional<User> findByUsername(String username);

    @Query(value = """
            SELECT * FROM users u
            WHERE username = :keyword OR email = :keyword
            """, nativeQuery = true
    )
    Optional<User> findByKeyword(String keyword);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query(value = """
            SELECT * FROM users
            WHERE lower(email) LIKE lower(:email)
            """, nativeQuery = true
    )
    Page<User> findAllByEmailPageable(String email, Pageable pageable);
}
