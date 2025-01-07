package com.ecommerce.fast_campus_ecommerce.service.impl;

import com.ecommerce.fast_campus_ecommerce.entity.Role;
import com.ecommerce.fast_campus_ecommerce.entity.User;
import com.ecommerce.fast_campus_ecommerce.model.UserInfo;
import com.ecommerce.fast_campus_ecommerce.repository.RoleRepository;
import com.ecommerce.fast_campus_ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User not found");
            return new UsernameNotFoundException("User not found: " + username);
        });
        List<Role> roles = roleRepository.findByUserId(user.getUserId());
        return UserInfo.builder()
                .user(user)
                .roles(roles)
                .build();
    }
}
