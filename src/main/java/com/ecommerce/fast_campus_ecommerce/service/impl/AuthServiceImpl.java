package com.ecommerce.fast_campus_ecommerce.service.impl;

import com.ecommerce.fast_campus_ecommerce.common.errors.InvalidPasswordException;
import com.ecommerce.fast_campus_ecommerce.model.AuthRequest;
import com.ecommerce.fast_campus_ecommerce.model.UserInfo;
import com.ecommerce.fast_campus_ecommerce.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthServiceImpl implements AuthService {

    AuthenticationManager authenticationManager;
    @Override
    public UserInfo authentiate(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                            authRequest.getPassword()));

            UserInfo user = (UserInfo) authentication.getPrincipal();
            return user;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new InvalidPasswordException("Invalid username or password");
        }
    }
}
