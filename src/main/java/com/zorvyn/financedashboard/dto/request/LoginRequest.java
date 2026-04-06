package com.zorvyn.financedashboard.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for login")
public record LoginRequest(

        @Schema(example = "admin@zorvyn.com",
                description = "Use admin@zorvyn.com / analyst@zorvyn.com / viewer@zorvyn.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Schema(example = "admin123",
                description = "admin123 / analyst123 / viewer123")
        @NotBlank(message = "Password is required")
        String password
) {}