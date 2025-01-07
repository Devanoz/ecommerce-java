package com.ecommerce.fast_campus_ecommerce.service.impl;

import com.ecommerce.fast_campus_ecommerce.common.errors.EmailAlreadyExistsException;
import com.ecommerce.fast_campus_ecommerce.common.errors.InvalidPasswordException;
import com.ecommerce.fast_campus_ecommerce.common.errors.UserNotFoundException;
import com.ecommerce.fast_campus_ecommerce.common.errors.UsernameAlreadyExistsException;
import com.ecommerce.fast_campus_ecommerce.entity.Role;
import com.ecommerce.fast_campus_ecommerce.entity.User;
import com.ecommerce.fast_campus_ecommerce.entity.UserRole;
import com.ecommerce.fast_campus_ecommerce.model.UserRegisterRequest;
import com.ecommerce.fast_campus_ecommerce.model.UserResponse;
import com.ecommerce.fast_campus_ecommerce.model.UserUpdateRequest;
import com.ecommerce.fast_campus_ecommerce.repository.RoleRepository;
import com.ecommerce.fast_campus_ecommerce.repository.UserRepository;
import com.ecommerce.fast_campus_ecommerce.repository.UserRoleRepository;
import com.ecommerce.fast_campus_ecommerce.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRegisterRequest userRegisterRequest) {
        if(existsByUsername(userRegisterRequest.getUsername())) {
            log.error("Username already exists");
            throw new IllegalArgumentException("Username already exists: " + userRegisterRequest.getUsername());
        }
        if(existsByEmail(userRegisterRequest.getEmail())) {
            log.error("Email already exists");
            throw new IllegalArgumentException("Email already exists: " + userRegisterRequest.getEmail());
        }
        if(!userRegisterRequest.getPassword().equals(userRegisterRequest.getPasswordConfirmation())) {
            log.error("Password and password confirm do not match");
            throw new IllegalArgumentException("Password and password confirm do not match");
        }
        User user = User.builder()
                .username(userRegisterRequest.getUsername())
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .isEnabled(true)
                .build();
        User savedUser = userRepository.save(user);
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> {
            log.error("Role not found");
            return new IllegalArgumentException("Role not found: ROLE_USER");
        });
        UserRole userRole = UserRole.builder()
                .id(UserRole.UserRoleId.builder()
                        .userId(savedUser.getUserId())
                        .roleId(role.getRoleId())
                        .build())
                .build();
        userRoleRepository.save(userRole);
        return UserResponse.fromUserAndRoles(savedUser, List.of(role));
    }

    @Override
    public UserResponse findById(Long userId) {
        User exsistingUser = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found");
            throw new UserNotFoundException("User not found with id: " + userId);
        });
        List<Role> existingUserRoles = roleRepository.findByUserId(userId);
        return UserResponse.fromUserAndRoles(exsistingUser, existingUserRoles);
    }

    @Override
    public UserResponse findByKeyword(String keyword) {
        User existingUser = userRepository.findByKeyword(keyword).orElseThrow(() -> {
            log.error("User not found");
            throw new UserNotFoundException("User not found with keyword: " + keyword);
        });
        List<Role> existingUserRoles = roleRepository.findByUserId(existingUser.getUserId());
        return UserResponse.fromUserAndRoles(existingUser, existingUserRoles);
    }

    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        // if user want to change it's password
        if (userUpdateRequest.getCurrentPassword() != null && userUpdateRequest.getNewPassword() != null) {
            if (!passwordEncoder.matches(userUpdateRequest.getCurrentPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }

            String encodedPassword = passwordEncoder.encode(userUpdateRequest.getNewPassword());
            user.setPassword(encodedPassword);
        }

        if (userUpdateRequest.getUsername() != null && !userUpdateRequest.getUsername().equals(user.getUsername())) {
            if (existsByUsername(userUpdateRequest.getUsername())) {
                throw new UsernameAlreadyExistsException(
                        "Username " + userUpdateRequest.getUsername() + " is already taken");
            }

            user.setUsername(userUpdateRequest.getUsername());
        }

        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().equals(user.getEmail())) {
            if (existsByEmail(userUpdateRequest.getEmail())) {
                throw new EmailAlreadyExistsException("Email " + userUpdateRequest.getEmail() + " is already taken");
            }

            user.setEmail(userUpdateRequest.getEmail());
        }
        List<Role> roles = roleRepository.findByUserId(user.getUserId());
        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if(!userRepository.existsById(userId)) {
            log.error("User not found");
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        userRoleRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
