package com.zorvyn.financedashboard.controller;

import com.zorvyn.financedashboard.dto.request.LoginRequest;
import com.zorvyn.financedashboard.dto.request.RegisterRequest;
import com.zorvyn.financedashboard.dto.response.ApiResponse;
import com.zorvyn.financedashboard.dto.response.AuthResponse;
import com.zorvyn.financedashboard.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = "Creates a new account with VIEWER role by default")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login",
            description = "Returns a JWT token to use in Authorization header" +
                    "Test credentials:\n" +
                    "            - admin@zorvyn.com / admin123 → full access\n" +
                    "            - analyst@zorvyn.com / analyst123 → read + dashboard\n" +
                    "            - viewer@zorvyn.com / viewer123 → read only")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout",
            description = "Invalidates the session client-side. " +
                    "Discard the JWT token after calling this.")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Logged out successfully. Please discard your token."));
    }



}