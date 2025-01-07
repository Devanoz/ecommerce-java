package com.ecommerce.fast_campus_ecommerce.controllers;

import com.ecommerce.fast_campus_ecommerce.model.*;
import com.ecommerce.fast_campus_ecommerce.service.AuthService;
import com.ecommerce.fast_campus_ecommerce.service.JwtService;
import com.ecommerce.fast_campus_ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    // POST /api/v1/auth/login
    @PostMapping("/login")
    @Operation(summary = "This endpoint does not require authentication", security = {})
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest authRequest
    ) {
        UserInfo userInfo = authService.authenticate(authRequest);
        String token = jwtService.generateToken(userInfo);
        AuthResponse authResponse = AuthResponse.fromUserInfo(userInfo, token);

        return ResponseEntity.ok(authResponse);
    }

    // POST /api/v1/auth/register
    @PostMapping("/register")
    @Operation(summary = "This endpoint does not require authentication", security = {})
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRegisterRequest registerRequest
    ) {
        UserResponse userResponse = userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponse);
    }

}
