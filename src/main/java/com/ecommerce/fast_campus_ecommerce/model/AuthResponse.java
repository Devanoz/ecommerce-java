package com.ecommerce.fast_campus_ecommerce.model;


import com.ecommerce.fast_campus_ecommerce.entity.Role;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthResponse {

    private String token;
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;

    public static AuthResponse fromUserInfo(UserInfo user, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getUser().getUserId())
                .username(user.getUsername())
                .email(user.getUser().getEmail())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }
}