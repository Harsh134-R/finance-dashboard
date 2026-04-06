package com.zorvyn.financedashboard.controller;

import com.zorvyn.financedashboard.dto.request.UpdateUserRoleRequest;
import com.zorvyn.financedashboard.dto.request.UpdateUserStatusRequest;
import com.zorvyn.financedashboard.dto.response.ApiResponse;
import com.zorvyn.financedashboard.dto.response.UserResponse;
import com.zorvyn.financedashboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management",
        description = "Admin only - manage users, roles, and status")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all users (Admin only)",
            description = "Paginated list of all users in the system",
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            description = "Page number (0-based)",
                            example = "0",
                            schema = @Schema(type = "integer", defaultValue = "0")
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            description = "Number of records per page",
                            example = "10",
                            schema = @Schema(type = "integer", defaultValue = "10")
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            description = "Sort field and direction",
                            example = "createdAt,desc",
                            schema = @Schema(type = "string", defaultValue = "createdAt,desc")
                    )
            }

    )
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(
                ApiResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile",
            description = "Returns the currently authenticated user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        UserResponse user = userService.getMyProfile();
        return ResponseEntity.ok(
                ApiResponse.success("Profile retrieved successfully", user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable UUID id) {

        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(
                ApiResponse.success("User retrieved successfully", user));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role (Admin only)",
            description = "Change a user's role to VIEWER, ANALYST, or ADMIN")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRoleRequest request) {

        UserResponse user = userService.updateRole(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("User role updated successfully", user));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user status (Admin only)",
            description = "Activate or deactivate a user account")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        UserResponse user = userService.updateStatus(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("User status updated successfully", user));
    }
}