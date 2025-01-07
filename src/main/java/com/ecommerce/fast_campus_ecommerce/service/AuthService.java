package com.ecommerce.fast_campus_ecommerce.service;

import com.ecommerce.fast_campus_ecommerce.model.AuthRequest;
import com.ecommerce.fast_campus_ecommerce.model.UserInfo;

public interface AuthService {
    UserInfo authenticate(AuthRequest authRequest);
}
