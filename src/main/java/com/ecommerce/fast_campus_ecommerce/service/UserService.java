package com.ecommerce.fast_campus_ecommerce.service;

import com.ecommerce.fast_campus_ecommerce.model.UserRegisterRequest;
import com.ecommerce.fast_campus_ecommerce.model.UserResponse;
import com.ecommerce.fast_campus_ecommerce.model.UserUpdateRequest;

public interface UserService {
    UserResponse registerUser(UserRegisterRequest userRegisterRequest);
    UserResponse findById(Long userId);
    UserResponse findByKeyword(String keyword);
    UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest);
    void deleteUser(Long userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
