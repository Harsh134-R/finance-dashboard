package com.project.financedashboard.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for login")
public record LoginRequest(

        @Schema(example = "admin@project.com",
                description = "Use admin@project.com / analyst@project.com / viewer@project.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Schema(example = "admin123",
                description = "admin123 / analyst123 / viewer123")
        @NotBlank(message = "Password is required")
        String password
) {}