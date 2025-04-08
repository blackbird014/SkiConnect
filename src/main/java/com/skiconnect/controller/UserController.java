// Controller for managing users and roles (e.g., POST /users, POST /users/{userId}/roles)

package com.skiconnect.controller;

import com.skiconnect.model.User;
import com.skiconnect.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user", description = "Create a new user with optional CV (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        // Consider adding existsByUsername/Email checks here too, similar to AuthController
        // if (userService.existsByUsername(request.getUsername())) { ... }
        // if (userService.existsByEmail(request.getEmail())) { ... }
        
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER"); // Default role for this endpoint?
        
        // Correctly map fields to service method
        User user = userService.createUser(
                request.getUsername(), 
                request.getName(), 
                request.getPassword(), 
                request.getEmail(),
                roles // Pass the roles set
        );
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        
        if (request.getCv() != null) {
            CVResponse cvResponse = new CVResponse();
            cvResponse.setExperience(request.getCv().getExperience());
            cvResponse.setQualifications(request.getCv().getQualifications());
            cvResponse.setBio(request.getCv().getBio());
            response.setCv(cvResponse);
        }
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign a role to a user", description = "Assign a role to a user (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned",
                    content = @Content(schema = @Schema(implementation = UserRolesResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserRolesResponse> assignRole(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestBody RoleAssignmentRequest request) {
        
        User user = userService.assignRole(userId, "ROLE_" + request.getRoleId());
        
        UserRolesResponse response = new UserRolesResponse();
        response.setUserId(user.getId());
        
        Set<RoleResponse> roles = new HashSet<>();
        for (String role : user.getRoles()) {
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(role.replace("ROLE_", ""));
            roleResponse.setName(role.replace("ROLE_", ""));
            roles.add(roleResponse);
        }
        response.setRoles(roles);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Request and Response classes
    public static class UserRequest {
        @NotBlank
        @Size(min = 3, max = 20)
        private String username;
        private String name;
        private String email;
        private String password;
        private CVRequest cv;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public CVRequest getCv() {
            return cv;
        }

        public void setCv(CVRequest cv) {
            this.cv = cv;
        }
    }

    public static class CVRequest {
        private String experience;
        private String qualifications;
        private String bio;

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public String getQualifications() {
            return qualifications;
        }

        public void setQualifications(String qualifications) {
            this.qualifications = qualifications;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }
    }

    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private CVResponse cv;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public CVResponse getCv() {
            return cv;
        }

        public void setCv(CVResponse cv) {
            this.cv = cv;
        }
    }

    public static class CVResponse {
        private String experience;
        private String qualifications;
        private String bio;

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public String getQualifications() {
            return qualifications;
        }

        public void setQualifications(String qualifications) {
            this.qualifications = qualifications;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }
    }

    public static class RoleAssignmentRequest {
        private String roleId;

        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }
    }

    public static class UserRolesResponse {
        private Long userId;
        private Set<RoleResponse> roles;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Set<RoleResponse> getRoles() {
            return roles;
        }

        public void setRoles(Set<RoleResponse> roles) {
            this.roles = roles;
        }
    }

    public static class RoleResponse {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
