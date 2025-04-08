package com.skiconnect.security;

import com.skiconnect.model.User;
import com.skiconnect.repository.UserRepository; // Assuming roles are stored in User entity
import com.skiconnect.service.UserService; // Import UserService
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository; // Keep if needed for signup or other logic

    @Autowired
    UserService userService; // Inject UserService for creating user

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                                             userDetails.getId(),
                                             userDetails.getUsername(),
                                             userDetails.getEmail(),
                                             roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account using UserService which handles password encoding
        Set<String> strRoles = signUpRequest.getRole();
        Set<String> roles = new HashSet<>();

        // Basic role assignment - adjust as needed for validation/default roles
        if (strRoles == null || strRoles.isEmpty()) {
            // Default role if none provided - might need refinement
            // e.g., throw error or assign a default 'ROLE_USER'
             roles.add("ROLE_USER"); // Example default
        } else {
            // Ensure roles start with ROLE_ prefix if needed by your hasAuthority checks
            strRoles.forEach(role -> {
                 if (!role.toUpperCase().startsWith("ROLE_")) {
                      roles.add("ROLE_" + role.toUpperCase());
                 } else {
                     roles.add(role.toUpperCase());
                 }
            });
        }

        // Use userService.createUser which handles password encoding
        try {
             userService.createUser(
                signUpRequest.getUsername(),
                signUpRequest.getName(),
                signUpRequest.getPassword(),
                signUpRequest.getEmail(),
                roles
             );
             return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            // Log the exception details
            // logger.error("Error during user registration: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error during registration: " + e.getMessage());
        }
    }
} 