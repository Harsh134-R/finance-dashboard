package com.project.financedashboard.dto.response;

import com.project.financedashboard.enums.Role;

import java.util.UUID;

public record AuthResponse(
        UUID id,
        String fullName,
        String email,
        Role role,
        String token,
        String tokenType
) {
    // convenience constructor — tokenType always "Bearer"
    public AuthResponse(UUID id, String fullName,
                        String email, Role role, String token) {
        this(id, fullName, email, role, token, "Bearer");
    }
}