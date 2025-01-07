package com.ecommerce.fast_campus_ecommerce.service;

import com.ecommerce.fast_campus_ecommerce.model.UserInfo;

public interface JwtService {
    String generateToken(UserInfo userInfo);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
}
