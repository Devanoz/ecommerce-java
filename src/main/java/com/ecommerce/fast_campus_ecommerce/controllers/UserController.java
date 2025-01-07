package com.ecommerce.fast_campus_ecommerce.controllers;

import com.ecommerce.fast_campus_ecommerce.common.errors.ForbiddenAccessException;
import com.ecommerce.fast_campus_ecommerce.model.UserInfo;
import com.ecommerce.fast_campus_ecommerce.model.UserResponse;
import com.ecommerce.fast_campus_ecommerce.model.UserUpdateRequest;
import com.ecommerce.fast_campus_ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        UserResponse userResponse = UserResponse.fromUserAndRoles(userInfo.getUser(),
                userInfo.getRoles());
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserUpdateRequest updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        if (userInfo.getUser().getUserId() != id && !userInfo.getAuthorities().contains("ROLE_ADMIN")) {
            throw new ForbiddenAccessException(
                    "user " + userInfo.getUsername() + " not allowed to update");
        }
        UserResponse updatedUser = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        if (userInfo.getUser().getUserId() != id && !userInfo.getAuthorities().contains("ROLE_ADMIN")) {
            throw new ForbiddenAccessException(
                    "user " + userInfo.getUsername() + " not allowed to delete");
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
