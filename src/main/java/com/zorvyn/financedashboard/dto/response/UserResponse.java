package com.zorvyn.financedashboard.dto.response;

import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.enums.Role;
import com.zorvyn.financedashboard.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        Role role,
        UserStatus status,
        LocalDateTime createdAt
) {
    // Static factory — converts entity to DTO cleanly
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}