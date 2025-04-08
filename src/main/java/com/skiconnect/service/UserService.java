package com.skiconnect.service;

import com.skiconnect.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;
import java.util.Set;

public interface UserService extends BaseService<User, Long>, UserDetailsService {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User createUser(String username, String name, String password, String email, Set<String> roles);
    User createAdminUser(String username, String password, String email);
    User assignRole(Long userId, String role);
    User removeRole(Long userId, String role);
} 